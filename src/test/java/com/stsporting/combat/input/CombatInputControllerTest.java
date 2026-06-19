package com.stsporting.combat.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.math.Rectangle;
import com.stsporting.combat.CombatState;
import com.stsporting.combat.action.ActionManager;
import com.stsporting.combat.card.AbstractCard;
import com.stsporting.combat.creature.Creature;
import com.stsporting.combat.creature.Player;
import com.stsporting.content.cards.CardId;
import com.stsporting.content.cards.CardLibrary;
import org.junit.jupiter.api.Test;

class CombatInputControllerTest {

    // Flat single-card layout: card centred at (1000,200), rect (950,100,100,200).
    private final HandLayout layout = new HandLayout(1000, 200, 100, 200, 120, 1000, 0, 0);
    private final Rectangle enemyBox = new Rectangle(800, 600, 200, 200);

    private static final float CARD_X = 1000;
    private static final float CARD_Y = 200;
    private static final float THRESHOLD = 400;

    private CombatState state;
    private ActionManager mgr;
    private Creature enemy;

    private CombatInputController controller(boolean allowed) {
        state = new CombatState(new Player());
        state.energy = 3;
        enemy = new Creature("e", 30);
        state.enemies.add(enemy);
        mgr = new ActionManager(state);
        TargetResolver targets = (vx, vy) -> enemyBox.contains(vx, vy) ? enemy : null;
        return new CombatInputController(state, mgr, layout, targets, THRESHOLD, () -> allowed);
    }

    private void addCard(CardId id) {
        AbstractCard c = CardLibrary.newCard(id);
        state.hand.add(c);
    }

    @Test
    void selfCardDraggedUpPastThresholdPlays() {
        CombatInputController in = controller(true);
        addCard(CardId.DEFEND);

        assertTrue(in.onTouchDown(CARD_X, CARD_Y));
        assertEquals(InputState.DRAGGING, in.state());
        boolean played = in.onTouchUp(CARD_X, 450); // above threshold
        mgr.runToCompletion();

        assertTrue(played);
        assertEquals(2, state.energy);
        assertEquals(5, state.player.block);
        assertTrue(state.hand.isEmpty());
        assertEquals(InputState.IDLE, in.state());
    }

    @Test
    void releaseBelowThresholdCancels() {
        CombatInputController in = controller(true);
        addCard(CardId.DEFEND);

        in.onTouchDown(CARD_X, CARD_Y);
        boolean played = in.onTouchUp(CARD_X, 250); // below threshold

        assertFalse(played);
        assertEquals(3, state.energy);
        assertEquals(1, state.hand.size());
        assertTrue(in.lastPlayRejected());
    }

    @Test
    void targetedCardDroppedOnEnemyPlays() {
        CombatInputController in = controller(true);
        addCard(CardId.STRIKE);

        assertTrue(in.onTouchDown(CARD_X, CARD_Y));
        assertEquals(InputState.TARGETING, in.state());
        boolean played = in.onTouchUp(900, 700); // inside enemy box
        mgr.runToCompletion();

        assertTrue(played);
        assertEquals(24, enemy.currentHp); // Strike 6
        assertTrue(state.hand.isEmpty());
    }

    @Test
    void targetedCardReleasedOffEnemyCancels() {
        CombatInputController in = controller(true);
        addCard(CardId.STRIKE);

        in.onTouchDown(CARD_X, CARD_Y);
        boolean played = in.onTouchUp(100, 100); // not on enemy

        assertFalse(played);
        assertEquals(30, enemy.currentHp);
        assertEquals(1, state.hand.size());
    }

    @Test
    void insufficientEnergyRejectsOnRelease() {
        CombatInputController in = controller(true);
        state.energy = 0;
        addCard(CardId.STRIKE);

        in.onTouchDown(CARD_X, CARD_Y);
        boolean played = in.onTouchUp(900, 700);

        assertFalse(played);
        assertEquals(30, enemy.currentHp);
        assertEquals(1, state.hand.size());
    }

    @Test
    void inputNotAllowedIgnoresPickup() {
        CombatInputController in = controller(false);
        addCard(CardId.STRIKE);

        assertFalse(in.onTouchDown(CARD_X, CARD_Y));
        assertEquals(InputState.IDLE, in.state());
    }

    @Test
    void hoverTracksCardUnderCursor() {
        CombatInputController in = controller(true);
        addCard(CardId.STRIKE);

        in.onMouseMoved(CARD_X, CARD_Y);
        assertEquals(InputState.HOVER, in.state());
        assertTrue(in.hoveredCard() != null);

        in.onMouseMoved(10, 10); // off any card
        assertEquals(InputState.IDLE, in.state());
    }
}

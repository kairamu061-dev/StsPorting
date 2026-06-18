package com.stsporting.combat.card;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.stsporting.combat.CombatState;
import com.stsporting.combat.action.ActionManager;
import com.stsporting.combat.creature.Creature;
import org.junit.jupiter.api.Test;

/** Draw, reshuffle, and hand-cap behaviour. */
class CardDrawTest {

    /** Minimal no-effect card for pile manipulation tests. */
    private static AbstractCard card(String id) {
        return new AbstractCard(id, id, CardType.SKILL, 1, CardTarget.NONE) {
            @Override
            public void use(Creature target, ActionManager mgr) {
            }
        };
    }

    private CombatState stateWithDrawPile(int count) {
        CombatState s = new CombatState();
        for (int i = 0; i < count; i++) {
            s.drawPile.add(card("d" + i));
        }
        return s;
    }

    @Test
    void drawMovesCardsFromDrawToHand() {
        CombatState s = stateWithDrawPile(5);
        CardDrawHelper.draw(s, 3);
        assertEquals(3, s.hand.size());
        assertEquals(2, s.drawPile.size());
    }

    @Test
    void drawReshufflesDiscardWhenDrawEmpty() {
        CombatState s = new CombatState();
        s.discardPile.add(card("a"));
        s.discardPile.add(card("b"));
        CardDrawHelper.draw(s, 2);
        assertEquals(2, s.hand.size());
        assertTrue(s.discardPile.isEmpty());
        assertTrue(s.drawPile.isEmpty());
    }

    @Test
    void drawStopsAtHandCap() {
        CombatState s = stateWithDrawPile(20);
        CardDrawHelper.draw(s, 15);
        assertEquals(CombatState.MAX_HAND, s.hand.size());
    }

    @Test
    void drawWithNothingLeftIsNoop() {
        CombatState s = new CombatState();
        CardDrawHelper.draw(s, 3);
        assertEquals(0, s.hand.size());
    }
}

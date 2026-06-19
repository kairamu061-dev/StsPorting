package com.stsporting.combat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.stsporting.combat.action.ActionManager;
import com.stsporting.combat.card.AbstractCard;
import com.stsporting.combat.card.CardTarget;
import com.stsporting.combat.card.PlayCardFlow;
import com.stsporting.combat.creature.Creature;
import com.stsporting.combat.creature.Player;
import com.stsporting.combat.creature.enemy.AbstractMonster;
import com.stsporting.combat.flow.TurnController;
import com.stsporting.content.cards.CardLibrary;
import com.stsporting.content.monsters.MonsterId;
import com.stsporting.content.monsters.MonsterLibrary;
import java.util.ArrayList;
import java.util.Random;
import org.junit.jupiter.api.Test;

/**
 * End-to-end headless playthrough of one combat with the real starter deck and
 * a Cultist: a simple "play all attacks, then end turn" strategy must reach
 * VICTORY deterministically. Exercises the full stack (turn-flow + cards +
 * enemy AI + powers + action queue + content) without any GUI.
 */
class CombatIntegrationTest {

    private CombatState state;
    private ActionManager mgr;
    private TurnController tc;

    private void setUp() {
        state = new CombatState(new Player());
        state.rng = new Random(7);
        state.drawPile.addAll(CardLibrary.starterDeck());
        AbstractMonster enemy = MonsterLibrary.newMonster(MonsterId.CULTIST);
        enemy.initialize(state.rng);
        state.enemies.add(enemy);
        mgr = new ActionManager(state);
        tc = new TurnController(state, mgr);
    }

    private boolean finished() {
        return tc.phase() == TurnController.Phase.FINISHED;
    }

    private void advanceUntilInputOrEnd() {
        int guard = 0;
        while (!tc.isPlayerInputAllowed() && !finished()) {
            tc.update(1f);
            if (++guard > 100_000) {
                fail("Combat did not reach a stable state");
            }
        }
    }

    private void resolveQueue() {
        int guard = 0;
        while (!mgr.isIdle()) {
            tc.update(1f);
            if (++guard > 100_000) {
                fail("Action queue did not converge");
            }
        }
    }

    @Test
    void playerWinsOneCombatAgainstCultist() {
        setUp();
        Creature enemy = state.enemies.get(0);

        for (int turn = 0; turn < 40 && !finished(); turn++) {
            advanceUntilInputOrEnd();
            if (finished()) {
                break;
            }
            // Play every affordable targeted attack at the enemy.
            boolean acted = true;
            while (acted && !finished()) {
                acted = false;
                for (AbstractCard c : new ArrayList<>(state.hand)) {
                    if (c.target == CardTarget.ENEMY && state.energy >= c.cost() && !enemy.isDead()) {
                        if (PlayCardFlow.resolve(mgr, c, enemy)) {
                            resolveQueue();
                            acted = true;
                            break;
                        }
                    }
                }
            }
            if (finished()) {
                break;
            }
            tc.requestEndTurn();
            advanceUntilInputOrEnd();
        }

        assertTrue(finished(), "combat should finish");
        assertTrue(enemy.isDead(), "enemy should be dead (victory)");
        assertFalse(state.player.isDead(), "player should survive");
    }

    @Test
    void deterministicForSameSeed() {
        setUp();
        int enemyMaxHp = state.enemies.get(0).maxHp;
        setUp();
        assertEquals(enemyMaxHp, state.enemies.get(0).maxHp);
    }
}

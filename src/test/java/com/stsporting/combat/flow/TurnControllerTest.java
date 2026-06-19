package com.stsporting.combat.flow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.stsporting.combat.CombatState;
import com.stsporting.combat.action.ActionManager;
import com.stsporting.combat.card.AbstractCard;
import com.stsporting.combat.card.CardTarget;
import com.stsporting.combat.card.CardType;
import com.stsporting.combat.creature.Creature;
import com.stsporting.combat.creature.Player;
import com.stsporting.combat.creature.enemy.AbstractMonster;
import com.stsporting.combat.creature.enemy.EnemyMove;
import com.stsporting.combat.creature.enemy.Moves;
import com.stsporting.combat.power.WeakPower;
import java.util.List;
import org.junit.jupiter.api.Test;

class TurnControllerTest {

    private static AbstractCard noop(String id) {
        return new AbstractCard(id, id, CardType.SKILL, 1, CardTarget.NONE) {
            @Override
            public void use(Creature target, ActionManager mgr) {
            }
        };
    }

    /** Enemy that always attacks for 10. */
    private static final class Striker extends AbstractMonster {
        Striker() {
            super("striker", "Striker", 40, 40);
        }

        @Override
        public List<EnemyMove> moves() {
            return List.of(Moves.attack("hit", 10, 1, 0));
        }
    }

    private static final class Fixture {
        final CombatState state;
        final ActionManager mgr;
        final TurnController tc;

        Fixture(int deckSize, Striker enemy) {
            state = new CombatState(new Player());
            for (int i = 0; i < deckSize; i++) {
                state.drawPile.add(noop("c" + i));
            }
            if (enemy != null) {
                enemy.initialize(new java.util.Random(1));
                state.enemies.add(enemy);
            }
            mgr = new ActionManager(state);
            tc = new TurnController(state, mgr);
        }

        void pump(int times) {
            for (int i = 0; i < times; i++) {
                tc.update(1f);
            }
        }
    }

    @Test
    void beginDrawsOpeningHandAndRefillsEnergy() {
        Fixture f = new Fixture(20, new Striker());
        f.pump(50);
        assertEquals(TurnController.Phase.PLAYER, f.tc.phase());
        assertEquals(5, f.state.hand.size());
        assertEquals(3, f.state.energy);
        assertTrue(f.tc.isPlayerInputAllowed());
    }

    @Test
    void endTurnLetsEnemyAttackThenReturnsToPlayer() {
        Fixture f = new Fixture(20, new Striker());
        f.pump(50); // through opening
        int hpBefore = f.state.player.currentHp;

        f.tc.requestEndTurn();
        f.pump(100);

        assertEquals(TurnController.Phase.PLAYER, f.tc.phase());
        assertEquals(hpBefore - 10, f.state.player.currentHp); // took the 10 attack
        assertEquals(3, f.state.energy);                       // refilled next turn
    }

    @Test
    void blockResetsAtPlayerTurnStart() {
        Fixture f = new Fixture(20, new Striker());
        f.pump(50);
        f.state.player.block = 7;

        f.tc.requestEndTurn();
        f.pump(100);

        assertEquals(0, f.state.player.block); // reset on the new player turn
    }

    @Test
    void weakDecaysAtPlayerTurnEnd() {
        Fixture f = new Fixture(20, new Striker());
        f.pump(50);
        WeakPower weak = new WeakPower();
        weak.amount = 2;
        f.state.player.powers.add(weak);

        f.tc.requestEndTurn();
        f.pump(100);

        assertEquals(1, weak.amount); // decayed by 1 at player turn end
    }

    @Test
    void allEnemiesDeadEndsCombat() {
        Striker enemy = new Striker();
        Fixture f = new Fixture(20, enemy);
        f.pump(50);
        enemy.currentHp = 0;
        f.pump(5);
        assertEquals(TurnController.Phase.FINISHED, f.tc.phase());
    }

    @Test
    void playerDeathEndsCombat() {
        Fixture f = new Fixture(20, new Striker());
        f.pump(50);
        f.state.player.currentHp = 8; // will die to the 10 attack

        f.tc.requestEndTurn();
        f.pump(100);

        assertEquals(TurnController.Phase.FINISHED, f.tc.phase());
        assertTrue(f.state.player.isDead());
    }
}

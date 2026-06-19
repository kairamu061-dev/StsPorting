package com.stsporting.combat.creature.enemy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.stsporting.combat.CombatState;
import com.stsporting.combat.action.ActionManager;
import com.stsporting.combat.creature.Player;
import com.stsporting.combat.power.VulnerablePower;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

class EnemyTest {

    /** Single-attack enemy so takeTurn is deterministic. */
    private static final class Striker extends AbstractMonster {
        Striker() {
            super("striker", "Striker", 40, 40);
        }

        @Override
        public List<EnemyMove> moves() {
            return List.of(Moves.attack("hit", 10, 1, 0));
        }
    }

    @Test
    void initializeRollsHpAndPicksMove() {
        Striker s = new Striker();
        s.initialize(new Random(1));
        assertEquals(40, s.maxHp);
        assertEquals(40, s.currentHp);
        assertNotNull(s.nextMove());
    }

    @Test
    void takeTurnDealsDamageToPlayer() {
        CombatState state = new CombatState(new Player());
        ActionManager mgr = new ActionManager(state);
        Striker s = new Striker();
        s.initialize(new Random(1));
        state.enemies.add(s);

        s.takeTurn(mgr, state.player);
        mgr.runToCompletion();

        assertEquals(70, state.player.currentHp); // 80 - 10
    }

    @Test
    void intentPreviewReflectsPlayerVulnerable() {
        Player player = new Player();
        VulnerablePower vuln = new VulnerablePower();
        vuln.amount = 2;
        player.powers.add(vuln);

        Striker s = new Striker();
        s.initialize(new Random(1));

        Intent intent = s.getIntent(player);
        assertTrue(intent.type.isAttack());
        assertEquals(15, intent.damage); // 10 * 1.5 vulnerable
    }

    @Test
    void selectionIsDeterministicForSameSeed() {
        List<EnemyMove> moves = List.of(
                Moves.attack("a", 6, 1, 0),
                Moves.defend("b", 5, 1, 0));
        EnemyMove first = MoveSelector.select(moves, new ArrayDeque<>(), new Random(42));
        EnemyMove second = MoveSelector.select(moves, new ArrayDeque<>(), new Random(42));
        assertEquals(first.id, second.id);
    }

    @Test
    void consecutiveLimitExcludesOverusedMove() {
        EnemyMove capped = Moves.attack("a", 6, 1, 2); // max 2 in a row
        EnemyMove other = Moves.defend("b", 5, 1, 0);
        Deque<String> history = new ArrayDeque<>(List.of("a", "a"));

        assertTrue(MoveSelector.exceedsConsecutive(capped, history));
        assertFalse(MoveSelector.exceedsConsecutive(other, history));

        // With "a" excluded, selection must return "b".
        EnemyMove pick = MoveSelector.select(List.of(capped, other), history, new Random(0));
        assertEquals("b", pick.id);
    }
}

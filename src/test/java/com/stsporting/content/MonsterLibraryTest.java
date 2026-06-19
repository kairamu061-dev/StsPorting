package com.stsporting.content;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.stsporting.combat.CombatState;
import com.stsporting.combat.action.ActionManager;
import com.stsporting.combat.creature.Player;
import com.stsporting.combat.creature.enemy.AbstractMonster;
import com.stsporting.combat.creature.enemy.IntentType;
import com.stsporting.combat.power.RitualPower;
import com.stsporting.combat.power.StrengthPower;
import com.stsporting.content.monsters.MonsterId;
import com.stsporting.content.monsters.MonsterLibrary;
import java.util.Random;
import org.junit.jupiter.api.Test;

class MonsterLibraryTest {

    @Test
    void cultistOpensWithRitualBuff() {
        AbstractMonster cultist = MonsterLibrary.newMonster(MonsterId.CULTIST);
        cultist.initialize(new Random(1));
        assertEquals(IntentType.BUFF, cultist.getIntent(new Player()).type);
    }

    @Test
    void cultistRitualThenGainsStrengthNextTurn() {
        CombatState state = new CombatState(new Player());
        ActionManager mgr = new ActionManager(state);
        AbstractMonster cultist = MonsterLibrary.newMonster(MonsterId.CULTIST);
        cultist.initialize(new Random(1));
        state.enemies.add(cultist);

        // Turn 1: ritual.
        cultist.takeTurn(mgr, state.player);
        mgr.runToCompletion();
        assertNotNull(cultist.getPower(RitualPower.class));

        // Start of turn 2: ritual grants Strength.
        for (var p : new java.util.ArrayList<>(cultist.powers)) {
            p.atStartOfTurn();
        }
        mgr.runToCompletion();
        StrengthPower str = cultist.getPower(StrengthPower.class);
        assertEquals(3, str == null ? 0 : str.amount);
    }

    @Test
    void jawWormInitializesWithHpAndIntent() {
        AbstractMonster jaw = MonsterLibrary.newMonster(MonsterId.JAW_WORM);
        jaw.initialize(new Random(3));
        assertTrue(jaw.maxHp >= 40 && jaw.maxHp <= 44);
        assertNotNull(jaw.nextMove());
    }
}

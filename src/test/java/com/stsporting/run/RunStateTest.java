package com.stsporting.run;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RunStateTest {

    @Test
    void newRunHasStarterDeckAndFullHp() {
        RunState rs = RunState.newRun(123);
        assertEquals(80, rs.maxHp);
        assertEquals(80, rs.currentHp);
        assertEquals(99, rs.gold);
        assertEquals(10, rs.masterDeck.size());
    }

    @Test
    void healAndLoseHpAreClamped() {
        RunState rs = RunState.newRun(1);
        rs.loseHp(30);
        assertEquals(50, rs.currentHp);
        rs.heal(100);
        assertEquals(80, rs.currentHp); // clamped to max
        rs.loseHp(200);
        assertEquals(0, rs.currentHp);
        assertTrue(rs.isDead());
        rs.heal(10); // cannot heal a corpse
        assertEquals(0, rs.currentHp);
    }

    @Test
    void increaseMaxHpRaisesBoth() {
        RunState rs = RunState.newRun(1);
        rs.loseHp(20); // 60/80
        rs.increaseMaxHp(8);
        assertEquals(88, rs.maxHp);
        assertEquals(68, rs.currentHp);
    }

    @Test
    void goldSpendRejectsWhenInsufficient() {
        RunState rs = RunState.newRun(1);
        assertTrue(rs.spendGold(50));
        assertEquals(49, rs.gold);
        assertFalse(rs.spendGold(100));
        assertEquals(49, rs.gold);
        rs.addGold(51);
        assertEquals(100, rs.gold);
    }
}

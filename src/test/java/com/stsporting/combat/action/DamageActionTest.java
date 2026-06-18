package com.stsporting.combat.action;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.stsporting.combat.CombatState;
import com.stsporting.combat.DamageType;
import com.stsporting.combat.creature.Creature;
import com.stsporting.combat.power.AbstractPower;
import com.stsporting.combat.power.StrengthPower;
import com.stsporting.combat.power.ThornsPower;
import com.stsporting.combat.power.VulnerablePower;
import com.stsporting.combat.power.WeakPower;
import org.junit.jupiter.api.Test;

/** Verifies the damage resolution order and on-attacked interrupts. */
class DamageActionTest {

    private ActionManager mgr;

    private ActionManager manager() {
        if (mgr == null) {
            mgr = new ActionManager(new CombatState());
        }
        return mgr;
    }

    private void addPower(Creature c, AbstractPower p, int amount) {
        p.attach(c, manager());
        p.amount = amount;
        c.powers.add(p);
    }

    @Test
    void plainDamageReducesHp() {
        Creature target = new Creature("dummy", 50);
        manager().addToBottom(new DamageAction(target, 6, null, DamageType.ATTACK));
        manager().runToCompletion();
        assertEquals(44, target.currentHp);
        assertEquals(0, target.block);
    }

    @Test
    void blockAbsorbsThenHp() {
        Creature target = new Creature("dummy", 50);
        target.block = 4;
        manager().addToBottom(new DamageAction(target, 6, null, DamageType.ATTACK));
        manager().runToCompletion();
        assertEquals(0, target.block);
        assertEquals(48, target.currentHp); // 6 - 4 block = 2 to HP
    }

    @Test
    void blockFullyAbsorbs() {
        Creature target = new Creature("dummy", 50);
        target.block = 10;
        manager().addToBottom(new DamageAction(target, 6, null, DamageType.ATTACK));
        manager().runToCompletion();
        assertEquals(4, target.block);
        assertEquals(50, target.currentHp);
    }

    @Test
    void modifiersApplyInOrderStrengthWeakVulnerable() {
        Creature source = new Creature("attacker", 50);
        Creature target = new Creature("dummy", 50);
        addPower(source, new StrengthPower(), 3);   // +3
        addPower(source, new WeakPower(), 1);        // x0.75 floor
        addPower(target, new VulnerablePower(), 2);  // x1.5 floor
        // 6 -> +3 = 9 -> *0.75 = 6 -> *1.5 = 9
        manager().addToBottom(new DamageAction(target, 6, source, DamageType.ATTACK));
        manager().runToCompletion();
        assertEquals(41, target.currentHp);
    }

    @Test
    void thornsReflectsBackToAttacker() {
        Creature source = new Creature("attacker", 50);
        Creature target = new Creature("dummy", 50);
        addPower(target, new ThornsPower(), 3);
        manager().addToBottom(new DamageAction(target, 6, source, DamageType.ATTACK));
        manager().runToCompletion();
        assertEquals(44, target.currentHp); // took 6
        assertEquals(47, source.currentHp); // thorns 3 back
    }
}

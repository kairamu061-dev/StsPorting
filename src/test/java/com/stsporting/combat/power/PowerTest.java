package com.stsporting.combat.power;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.stsporting.combat.CombatState;
import com.stsporting.combat.action.ActionManager;
import com.stsporting.combat.action.ApplyPowerAction;
import com.stsporting.combat.creature.Creature;
import org.junit.jupiter.api.Test;

/** Stacking, decay, and turn-boundary behaviour of core powers. */
class PowerTest {

    private ActionManager newManager() {
        return new ActionManager(new CombatState());
    }

    private void addPower(Creature c, AbstractPower p, int amount, ActionManager mgr) {
        p.attach(c, mgr);
        p.amount = amount;
        c.powers.add(p);
    }

    @Test
    void applyingSamePowerStacksAmount() {
        ActionManager mgr = newManager();
        Creature c = new Creature("c", 50);
        mgr.addToBottom(new ApplyPowerAction(c, new StrengthPower(), 2));
        mgr.addToBottom(new ApplyPowerAction(c, new StrengthPower(), 3));
        mgr.runToCompletion();
        assertEquals(5, c.getPower(StrengthPower.class).amount);
    }

    @Test
    void poisonLosesHpAndDecaysAtTurnStart() {
        ActionManager mgr = newManager();
        Creature c = new Creature("c", 50);
        PoisonPower poison = new PoisonPower();
        addPower(c, poison, 5, mgr);

        poison.atStartOfTurn();
        mgr.runToCompletion();

        assertEquals(45, c.currentHp);
        assertEquals(4, poison.amount);
    }

    @Test
    void metallicizeGrantsBlockAtTurnEnd() {
        ActionManager mgr = newManager();
        Creature c = new Creature("c", 50);
        MetallicizePower metal = new MetallicizePower();
        addPower(c, metal, 3, mgr);

        metal.atEndOfTurn(true);
        mgr.runToCompletion();

        assertEquals(3, c.block);
    }

    @Test
    void regenHealsAndDecays() {
        ActionManager mgr = newManager();
        Creature c = new Creature("c", 50);
        c.currentHp = 40;
        RegenPower regen = new RegenPower();
        addPower(c, regen, 3, mgr);

        regen.atStartOfTurn();

        assertEquals(43, c.currentHp);
        assertEquals(2, regen.amount);
    }

    @Test
    void vulnerableDecaysAndIsRemovedAtZero() {
        Creature c = new Creature("c", 50);
        VulnerablePower vuln = new VulnerablePower();
        addPower(c, vuln, 1, newManager());

        vuln.reducePerTurn();
        assertEquals(0, vuln.amount);
        // Removal happens via ReducePowerAction/ApplyPowerAction; shouldRemove flags it.
        org.junit.jupiter.api.Assertions.assertEquals(true, vuln.shouldRemove());
    }

    @Test
    void debuffNotAddedWhenAppliedAtZero() {
        ActionManager mgr = newManager();
        Creature c = new Creature("c", 50);
        mgr.addToBottom(new ApplyPowerAction(c, new WeakPower(), 0));
        mgr.runToCompletion();
        assertNull(c.getPower(WeakPower.class));
    }
}

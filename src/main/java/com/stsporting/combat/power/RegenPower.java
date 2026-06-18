package com.stsporting.combat.power;

/** Regen: at the owner's turn start, heal equal to stacks, then decay by 1. */
public class RegenPower extends AbstractPower {
    public RegenPower() {
        super("Regen", "Regeneration", PowerType.BUFF);
    }

    @Override
    public void atStartOfTurn() {
        if (owner != null && amount > 0) {
            owner.heal(amount);
            amount--;
        }
    }
}

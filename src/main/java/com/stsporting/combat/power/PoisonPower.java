package com.stsporting.combat.power;

import com.stsporting.combat.action.LoseHpAction;

/** Poison: at the owner's turn start, lose HP equal to stacks, then decay by 1. */
public class PoisonPower extends AbstractPower {
    public PoisonPower() {
        super("Poison", "Poison", PowerType.DEBUFF);
    }

    @Override
    public void atStartOfTurn() {
        if (mgr != null && amount > 0) {
            mgr.addToBottom(new LoseHpAction(owner, amount));
            amount--;
        }
    }
}

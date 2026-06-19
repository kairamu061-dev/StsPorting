package com.stsporting.combat.power;

import com.stsporting.combat.action.ApplyPowerAction;

/** Ritual: at the owner's turn start, gain Strength equal to stacks. */
public class RitualPower extends AbstractPower {
    public RitualPower() {
        super("Ritual", "Ritual", PowerType.BUFF);
    }

    @Override
    public void atStartOfTurn() {
        if (mgr != null && amount > 0) {
            mgr.addToBottom(new ApplyPowerAction(owner, new StrengthPower(), amount));
        }
    }
}

package com.stsporting.combat.power;

import com.stsporting.combat.action.GainBlockAction;

/** Metallicize: at the owner's turn end, gain block equal to stacks. */
public class MetallicizePower extends AbstractPower {
    public MetallicizePower() {
        super("Metallicize", "Metallicize", PowerType.BUFF);
    }

    @Override
    public void atEndOfTurn(boolean isPlayerTurn) {
        if (mgr != null && amount > 0) {
            mgr.addToBottom(new GainBlockAction(owner, amount));
        }
    }
}

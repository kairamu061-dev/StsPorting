package com.stsporting.combat.power;

import com.stsporting.combat.DamageInfo;
import com.stsporting.combat.DamageType;
import com.stsporting.combat.action.DamageAction;

/** Thorns: when the owner is attacked, deal {@code amount} back to the attacker. */
public class ThornsPower extends AbstractPower {
    public ThornsPower() {
        super("Thorns", "Thorns", PowerType.BUFF);
    }

    @Override
    public void onAttacked(DamageInfo info, int damageDealt) {
        if (info.attacker != null && info.type == DamageType.ATTACK && mgr != null) {
            mgr.addToTop(new DamageAction(info.attacker, amount, owner, DamageType.THORNS));
        }
    }
}

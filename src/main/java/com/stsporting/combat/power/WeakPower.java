package com.stsporting.combat.power;

import com.stsporting.combat.DamageType;

/** Weak: the owner's attacks deal 25% less (x0.75, floored). Decays 1/turn. */
public class WeakPower extends AbstractPower {
    public WeakPower() {
        super("Weak", "Weak", PowerType.DEBUFF);
    }

    @Override
    public int atDamageGive(int damage, DamageType type) {
        return type == DamageType.ATTACK ? (int) Math.floor(damage * 0.75f) : damage;
    }

    @Override
    public void reducePerTurn() {
        amount--;
    }
}

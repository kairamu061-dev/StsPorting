package com.stsporting.combat.power;

import com.stsporting.combat.DamageType;

/** Vulnerable: the owner takes 50% more attack damage (x1.5, floored). Decays 1/turn. */
public class VulnerablePower extends AbstractPower {
    public VulnerablePower() {
        super("Vulnerable", "Vulnerable", PowerType.DEBUFF);
    }

    @Override
    public int atDamageReceive(int damage, DamageType type) {
        return type == DamageType.ATTACK ? (int) Math.floor(damage * 1.5f) : damage;
    }

    @Override
    public void reducePerTurn() {
        amount--;
    }
}

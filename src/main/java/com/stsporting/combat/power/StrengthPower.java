package com.stsporting.combat.power;

import com.stsporting.combat.DamageType;

/** Adds its amount to attack damage. Permanent; may be negative (Strength down). */
public class StrengthPower extends AbstractPower {
    public StrengthPower() {
        super("Strength", "Strength", PowerType.BUFF);
    }

    @Override
    public int atDamageGive(int damage, DamageType type) {
        return type == DamageType.ATTACK ? damage + amount : damage;
    }

    @Override
    public boolean shouldRemove() {
        // Strength persists even at/below 0 (negative = Strength down).
        return false;
    }
}

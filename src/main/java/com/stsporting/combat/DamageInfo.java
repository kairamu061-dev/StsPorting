package com.stsporting.combat;

import com.stsporting.combat.creature.Creature;

/** Context passed to on-attacked hooks: who attacked, the base amount and type. */
public class DamageInfo {
    public final Creature attacker;
    public final int base;
    public final DamageType type;

    public DamageInfo(Creature attacker, int base, DamageType type) {
        this.attacker = attacker;
        this.base = base;
        this.type = type;
    }
}

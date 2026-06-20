package com.stsporting.combat;

import com.stsporting.combat.creature.Creature;
import com.stsporting.combat.power.AbstractPower;

/**
 * Hook for the presentation layer to react to combat events (spawn numbers,
 * flashes, shakes) without the logic layer depending on rendering. All methods
 * default to no-ops, so headless logic uses {@link #NO_OP} and stays GL-free.
 */
public interface CombatListener {
    CombatListener NO_OP = new CombatListener() {
    };

    /** Damage that reached HP after block. {@code hpDamage} may be 0 if fully blocked. */
    default void onDamageDealt(Creature target, int hpDamage, DamageType type) {
    }

    default void onBlockGained(Creature target, int amount) {
    }

    default void onHpLost(Creature target, int amount) {
    }

    default void onPowerApplied(Creature target, AbstractPower power, int amount) {
    }
}

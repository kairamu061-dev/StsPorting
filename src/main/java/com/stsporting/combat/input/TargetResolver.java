package com.stsporting.combat.input;

import com.stsporting.combat.creature.Creature;

/** Resolves which enemy (if any) is under a virtual-space point. */
@FunctionalInterface
public interface TargetResolver {
    Creature enemyAt(float vx, float vy);
}

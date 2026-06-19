package com.stsporting.combat.flow;

import com.stsporting.combat.CombatState;

/**
 * Energy as a combat resource: refilled to max each player turn (no carryover),
 * spent to play cards, fully drained for X-cost. Operates on
 * {@link CombatState#energy} so there is a single source of truth.
 */
public class EnergyManager {
    private final CombatState state;

    public EnergyManager(CombatState state) {
        this.state = state;
    }

    public void refill() {
        state.energy = state.maxEnergy;
    }

    public boolean canAfford(int cost) {
        return state.energy >= cost;
    }

    public boolean spend(int cost) {
        if (state.energy < cost) {
            return false;
        }
        state.energy -= cost;
        return true;
    }

    /** Spend all remaining energy and return the amount (for X-cost cards). */
    public int spendAll() {
        int x = state.energy;
        state.energy = 0;
        return x;
    }
}

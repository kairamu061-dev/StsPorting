package com.stsporting.combat.creature;

import com.stsporting.combat.power.AbstractPower;
import java.util.ArrayList;
import java.util.List;

/**
 * Common base for the player and enemies: hit points, block, and a list of
 * powers (buffs/debuffs) kept in application order. Pure state — combat actions
 * mutate it; it owns no rendering.
 */
public class Creature {
    public String name;
    public int maxHp;
    public int currentHp;
    public int block;
    public final List<AbstractPower> powers = new ArrayList<>();

    public Creature() {
    }

    public Creature(String name, int maxHp) {
        this.name = name;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
    }

    public boolean isDead() {
        return currentHp <= 0;
    }

    /** Heal, clamped to maxHp. No-op if dead (cannot heal a corpse). */
    public void heal(int amount) {
        if (isDead() || amount <= 0) {
            return;
        }
        currentHp = Math.min(maxHp, currentHp + amount);
    }

    public void addBlock(int amount) {
        if (amount > 0) {
            block += amount;
        }
    }

    /** First power that is an instance of the given class, or null. */
    public <T extends AbstractPower> T getPower(Class<T> cls) {
        for (AbstractPower p : powers) {
            if (cls.isInstance(p)) {
                return cls.cast(p);
            }
        }
        return null;
    }
}

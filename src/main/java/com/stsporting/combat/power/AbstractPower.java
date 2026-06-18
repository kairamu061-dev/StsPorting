package com.stsporting.combat.power;

import com.stsporting.combat.DamageInfo;
import com.stsporting.combat.DamageType;
import com.stsporting.combat.action.ActionManager;
import com.stsporting.combat.creature.Creature;

/**
 * Base for buffs/debuffs. Each power overrides only the hooks it cares about;
 * defaults are no-ops. {@code amount} is the stack count. Hooks that need to
 * queue follow-up effects (e.g. Thorns) use the attached {@link ActionManager}.
 */
public abstract class AbstractPower {
    public String id;
    public String name;
    public PowerType type;
    public int amount;
    public Creature owner;
    protected ActionManager mgr;

    protected AbstractPower(String id, String name, PowerType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    /** Wire up owner and action manager when the power is applied. */
    public void attach(Creature owner, ActionManager mgr) {
        this.owner = owner;
        this.mgr = mgr;
    }

    // --- Damage modifiers (return the adjusted amount) ---

    /** Modify outgoing damage dealt by the owner. */
    public int atDamageGive(int damage, DamageType type) {
        return damage;
    }

    /** Modify incoming damage received by the owner. */
    public int atDamageReceive(int damage, DamageType type) {
        return damage;
    }

    /** Modify block being granted to the owner. */
    public int modifyBlock(int amount) {
        return amount;
    }

    // --- Triggers ---

    /** Owner was attacked (after HP applied). Use mgr.addToTop for reactions. */
    public void onAttacked(DamageInfo info, int damageDealt) {
    }

    /** Start of the owner's turn. */
    public void atStartOfTurn() {
    }

    /** End of a turn. isPlayerTurn distinguishes whose turn ended. */
    public void atEndOfTurn(boolean isPlayerTurn) {
    }

    // --- Stack management ---

    public void stack(int delta) {
        amount += delta;
    }

    /** Per-turn natural decay (e.g. Weak/Vulnerable). Default: none. */
    public void reducePerTurn() {
    }

    /** Debuffs vanish at 0 stacks; buffs may persist (e.g. negative Strength). */
    public boolean shouldRemove() {
        return type == PowerType.DEBUFF && amount <= 0;
    }
}

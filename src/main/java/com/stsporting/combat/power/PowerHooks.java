package com.stsporting.combat.power;

import com.stsporting.combat.DamageType;
import com.stsporting.combat.creature.Creature;

/**
 * Cross-cutting helpers that fold a creature's powers over a value in
 * application order. Used by DamageAction/GainBlockAction so the modifier order
 * is defined in one place (Strength -> Weak on give, Vulnerable on receive).
 */
public final class PowerHooks {
    private PowerHooks() {
    }

    public static int applyDamageGive(Creature attacker, int damage, DamageType type) {
        int d = damage;
        for (AbstractPower p : attacker.powers) {
            d = p.atDamageGive(d, type);
        }
        return d;
    }

    public static int applyDamageReceive(Creature target, int damage, DamageType type) {
        int d = damage;
        for (AbstractPower p : target.powers) {
            d = p.atDamageReceive(d, type);
        }
        return d;
    }

    public static int applyBlockModifiers(Creature owner, int amount) {
        int a = amount;
        for (AbstractPower p : owner.powers) {
            a = p.modifyBlock(a);
        }
        return a;
    }
}

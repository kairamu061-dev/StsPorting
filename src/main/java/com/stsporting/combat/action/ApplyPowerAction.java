package com.stsporting.combat.action;

import com.stsporting.combat.creature.Creature;
import com.stsporting.combat.power.AbstractPower;

/**
 * Applies a power to a target: if the same power class is present, its stacks
 * are increased by {@code amount}; otherwise the new power is attached with the
 * given amount. Debuffs that end up at 0 are removed.
 */
public class ApplyPowerAction extends GameAction {
    private final Creature target;
    private final AbstractPower power;
    private final int amount;
    private boolean applied;

    public ApplyPowerAction(Creature target, AbstractPower power, int amount) {
        this.target = target;
        this.power = power;
        this.amount = amount;
        this.duration = 0.1f;
        this.startDuration = 0.1f;
    }

    @Override
    public void update(float delta) {
        if (!applied) {
            applied = true;
            apply();
        }
        tickDuration(delta);
    }

    private void apply() {
        if (target.isDead()) {
            return;
        }
        AbstractPower existing = target.getPower(power.getClass());
        if (existing != null) {
            existing.stack(amount);
            if (existing.shouldRemove()) {
                target.powers.remove(existing);
            }
        } else {
            power.attach(target, mgr);
            power.amount = amount;
            if (!power.shouldRemove()) {
                target.powers.add(power);
            }
        }
    }
}

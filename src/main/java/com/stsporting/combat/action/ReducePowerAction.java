package com.stsporting.combat.action;

import com.stsporting.combat.creature.Creature;
import com.stsporting.combat.power.AbstractPower;

/** Reduces a power's stacks; removes it when it drops to 0 or below. */
public class ReducePowerAction extends GameAction {
    private final Creature target;
    private final Class<? extends AbstractPower> powerClass;
    private final int amount;
    private boolean applied;

    public ReducePowerAction(Creature target, Class<? extends AbstractPower> powerClass, int amount) {
        this.target = target;
        this.powerClass = powerClass;
        this.amount = amount;
        this.duration = 0.05f;
        this.startDuration = 0.05f;
    }

    @Override
    public void update(float delta) {
        if (!applied) {
            applied = true;
            AbstractPower p = target.getPower(powerClass);
            if (p != null) {
                p.stack(-amount);
                if (p.amount <= 0) {
                    target.powers.remove(p);
                }
            }
        }
        tickDuration(delta);
    }
}

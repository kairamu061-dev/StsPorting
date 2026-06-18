package com.stsporting.combat.action;

import com.stsporting.combat.creature.Creature;

/** Direct HP loss that bypasses block (e.g. Poison, self-damage). */
public class LoseHpAction extends GameAction {
    private final Creature target;
    private final int amount;
    private boolean applied;

    public LoseHpAction(Creature target, int amount) {
        this.target = target;
        this.amount = amount;
        this.duration = 0.1f;
        this.startDuration = 0.1f;
    }

    @Override
    public void update(float delta) {
        if (!applied) {
            applied = true;
            if (!target.isDead()) {
                target.currentHp = Math.max(0, target.currentHp - amount);
            }
        }
        tickDuration(delta);
    }
}

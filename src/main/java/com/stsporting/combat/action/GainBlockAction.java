package com.stsporting.combat.action;

import com.stsporting.combat.creature.Creature;
import com.stsporting.combat.power.PowerHooks;

/** Grants block to a creature, after applying any block-modifier powers. */
public class GainBlockAction extends GameAction {
    private final Creature target;
    private final int amount;
    private boolean applied;

    public GainBlockAction(Creature target, int amount) {
        this.target = target;
        this.amount = amount;
        this.duration = 0.1f;
        this.startDuration = 0.1f;
    }

    @Override
    public void update(float delta) {
        if (!applied) {
            applied = true;
            int a = PowerHooks.applyBlockModifiers(target, amount);
            target.addBlock(a);
            state.listener.onBlockGained(target, a);
        }
        tickDuration(delta);
    }
}

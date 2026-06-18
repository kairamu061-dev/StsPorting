package com.stsporting.combat.card;

import com.stsporting.combat.action.GameAction;

/** Draws n cards (with reshuffle and hand cap handled by CardDrawHelper). */
public class DrawAction extends GameAction {
    private final int n;
    private boolean applied;

    public DrawAction(int n) {
        this.n = n;
        this.duration = 0.1f;
        this.startDuration = 0.1f;
    }

    @Override
    public void update(float delta) {
        if (!applied) {
            applied = true;
            CardDrawHelper.draw(state, n);
        }
        tickDuration(delta);
    }
}

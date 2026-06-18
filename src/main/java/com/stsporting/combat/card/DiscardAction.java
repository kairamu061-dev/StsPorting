package com.stsporting.combat.card;

import com.stsporting.combat.action.GameAction;

/** Moves a specific card from hand to the discard pile. */
public class DiscardAction extends GameAction {
    private final AbstractCard card;
    private boolean applied;

    public DiscardAction(AbstractCard card) {
        this.card = card;
        this.duration = 0.1f;
        this.startDuration = 0.1f;
    }

    @Override
    public void update(float delta) {
        if (!applied) {
            applied = true;
            if (state.hand.remove(card)) {
                state.discardPile.add(card);
            }
        }
        tickDuration(delta);
    }
}

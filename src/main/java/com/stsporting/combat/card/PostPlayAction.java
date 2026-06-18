package com.stsporting.combat.card;

import com.stsporting.combat.action.GameAction;

/**
 * Routes a played card to its resting place <em>after</em> its effects resolve:
 * POWER cards are consumed (kept nowhere), exhaust cards go to the exhaust pile,
 * everything else goes to the discard pile. Queued after the card's effect
 * actions so the "effects then move" order matches the original.
 */
public class PostPlayAction extends GameAction {
    private final AbstractCard card;
    private boolean applied;

    public PostPlayAction(AbstractCard card) {
        this.card = card;
        this.duration = 0f;
        this.startDuration = 0f;
    }

    @Override
    public void update(float delta) {
        if (!applied) {
            applied = true;
            if (card.type == CardType.POWER) {
                // Consumed: a power card lives on the board as a power, not a pile.
            } else if (card.exhaust) {
                state.exhaustPile.add(card);
            } else {
                state.discardPile.add(card);
            }
        }
        isDone = true;
    }
}

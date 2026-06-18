package com.stsporting.combat.card;

import com.stsporting.combat.creature.Creature;

/** A pending play request: a card and its chosen target (null if untargeted). */
public class CardQueueItem {
    public final AbstractCard card;
    public final Creature target;

    public CardQueueItem(AbstractCard card, Creature target) {
        this.card = card;
        this.target = target;
    }
}

package com.stsporting.content.cards;

import com.stsporting.combat.action.ActionManager;
import com.stsporting.combat.action.GainBlockAction;
import com.stsporting.combat.card.AbstractCard;
import com.stsporting.combat.card.CardTarget;
import com.stsporting.combat.card.CardType;
import com.stsporting.combat.creature.Creature;

/** Defend: gain 5 (8 upgraded) block. */
public class Defend extends AbstractCard {
    public Defend() {
        super("defend", "Defend", CardType.SKILL, 1, CardTarget.SELF);
    }

    private int block() {
        return upgraded ? 8 : 5;
    }

    @Override
    public void use(Creature target, ActionManager mgr) {
        mgr.addToBottom(new GainBlockAction(mgr.state().player, block()));
    }
}

package com.stsporting.content.cards;

import com.stsporting.combat.DamageType;
import com.stsporting.combat.action.ActionManager;
import com.stsporting.combat.action.DamageAction;
import com.stsporting.combat.card.AbstractCard;
import com.stsporting.combat.card.CardTarget;
import com.stsporting.combat.card.CardType;
import com.stsporting.combat.creature.Creature;

/** Strike: deal 6 (9 upgraded) damage to a single enemy. */
public class Strike extends AbstractCard {
    public Strike() {
        super("strike", "Strike", CardType.ATTACK, 1, CardTarget.ENEMY);
    }

    private int damage() {
        return upgraded ? 9 : 6;
    }

    @Override
    public void use(Creature target, ActionManager mgr) {
        mgr.addToBottom(new DamageAction(target, damage(), mgr.state().player, DamageType.ATTACK));
    }
}

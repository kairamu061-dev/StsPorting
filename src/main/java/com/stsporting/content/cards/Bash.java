package com.stsporting.content.cards;

import com.stsporting.combat.DamageType;
import com.stsporting.combat.action.ActionManager;
import com.stsporting.combat.action.ApplyPowerAction;
import com.stsporting.combat.action.DamageAction;
import com.stsporting.combat.card.AbstractCard;
import com.stsporting.combat.card.CardTarget;
import com.stsporting.combat.card.CardType;
import com.stsporting.combat.creature.Creature;
import com.stsporting.combat.power.VulnerablePower;

/** Bash: deal 8 (10) damage and apply 2 (3) Vulnerable to a single enemy. */
public class Bash extends AbstractCard {
    public Bash() {
        super("bash", "Bash", CardType.ATTACK, 2, CardTarget.ENEMY);
    }

    private int damage() {
        return upgraded ? 10 : 8;
    }

    private int vulnerable() {
        return upgraded ? 3 : 2;
    }

    @Override
    public void use(Creature target, ActionManager mgr) {
        mgr.addToBottom(new DamageAction(target, damage(), mgr.state().player, DamageType.ATTACK));
        mgr.addToBottom(new ApplyPowerAction(target, new VulnerablePower(), vulnerable()));
    }
}

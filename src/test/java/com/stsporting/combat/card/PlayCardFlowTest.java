package com.stsporting.combat.card;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.stsporting.combat.CombatState;
import com.stsporting.combat.DamageType;
import com.stsporting.combat.action.ActionManager;
import com.stsporting.combat.action.DamageAction;
import com.stsporting.combat.creature.Creature;
import com.stsporting.combat.creature.Player;
import org.junit.jupiter.api.Test;

/** Play validation, energy spend, effect resolution and post-play routing. */
class PlayCardFlowTest {

    private static final class StrikeLike extends AbstractCard {
        StrikeLike() {
            super("strike", "Strike", CardType.ATTACK, 1, CardTarget.ENEMY);
        }

        @Override
        public void use(Creature target, ActionManager mgr) {
            mgr.addToBottom(new DamageAction(target, 6, mgr.state().player, DamageType.ATTACK));
        }
    }

    private static final class ExhaustSkill extends AbstractCard {
        ExhaustSkill() {
            super("flash", "Flash", CardType.SKILL, 0, CardTarget.NONE);
            this.exhaust = true;
        }

        @Override
        public void use(Creature target, ActionManager mgr) {
        }
    }

    private static final class PowerCard extends AbstractCard {
        PowerCard() {
            super("inflame", "Inflame", CardType.POWER, 1, CardTarget.SELF);
        }

        @Override
        public void use(Creature target, ActionManager mgr) {
        }
    }

    private ActionManager combat(Creature enemy, int energy) {
        CombatState s = new CombatState(new Player());
        s.energy = energy;
        if (enemy != null) {
            s.enemies.add(enemy);
        }
        return new ActionManager(s);
    }

    @Test
    void playingAttackSpendsEnergyDealsDamageAndDiscards() {
        Creature enemy = new Creature("e", 30);
        ActionManager mgr = combat(enemy, 3);
        AbstractCard card = new StrikeLike();
        mgr.state().hand.add(card);

        boolean ok = PlayCardFlow.resolve(mgr, card, enemy);
        mgr.runToCompletion();

        assertTrue(ok);
        assertEquals(2, mgr.state().energy);
        assertEquals(24, enemy.currentHp);
        assertFalse(mgr.state().hand.contains(card));
        assertTrue(mgr.state().discardPile.contains(card));
    }

    @Test
    void insufficientEnergyRejectsPlay() {
        Creature enemy = new Creature("e", 30);
        ActionManager mgr = combat(enemy, 0);
        AbstractCard card = new StrikeLike();
        mgr.state().hand.add(card);

        boolean ok = PlayCardFlow.resolve(mgr, card, enemy);

        assertFalse(ok);
        assertEquals(0, mgr.state().energy);
        assertEquals(30, enemy.currentHp);
        assertTrue(mgr.state().hand.contains(card));
    }

    @Test
    void exhaustCardGoesToExhaustPile() {
        ActionManager mgr = combat(null, 3);
        AbstractCard card = new ExhaustSkill();
        mgr.state().hand.add(card);

        PlayCardFlow.resolve(mgr, card, null);
        mgr.runToCompletion();

        assertTrue(mgr.state().exhaustPile.contains(card));
        assertFalse(mgr.state().discardPile.contains(card));
    }

    @Test
    void powerCardIsConsumedNotKeptInAnyPile() {
        ActionManager mgr = combat(null, 3);
        AbstractCard card = new PowerCard();
        mgr.state().hand.add(card);

        PlayCardFlow.resolve(mgr, card, null);
        mgr.runToCompletion();

        assertFalse(mgr.state().hand.contains(card));
        assertFalse(mgr.state().discardPile.contains(card));
        assertFalse(mgr.state().exhaustPile.contains(card));
    }

    @Test
    void targetedCardWithoutTargetIsRejected() {
        ActionManager mgr = combat(null, 3);
        AbstractCard card = new StrikeLike();
        mgr.state().hand.add(card);

        boolean ok = PlayCardFlow.resolve(mgr, card, null);

        assertFalse(ok);
        assertTrue(mgr.state().hand.contains(card));
    }
}

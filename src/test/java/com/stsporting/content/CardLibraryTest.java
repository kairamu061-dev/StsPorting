package com.stsporting.content;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.stsporting.combat.CombatState;
import com.stsporting.combat.action.ActionManager;
import com.stsporting.combat.card.AbstractCard;
import com.stsporting.combat.creature.Creature;
import com.stsporting.combat.creature.Player;
import com.stsporting.combat.power.VulnerablePower;
import com.stsporting.content.cards.CardId;
import com.stsporting.content.cards.CardLibrary;
import java.util.List;
import org.junit.jupiter.api.Test;

class CardLibraryTest {

    private ActionManager combat(Creature enemy) {
        CombatState s = new CombatState(new Player());
        if (enemy != null) {
            s.enemies.add(enemy);
        }
        return new ActionManager(s);
    }

    @Test
    void starterDeckHasExpectedComposition() {
        List<AbstractCard> deck = CardLibrary.starterDeck();
        assertEquals(10, deck.size());
        assertEquals(5, deck.stream().filter(c -> c.id.equals("strike")).count());
        assertEquals(4, deck.stream().filter(c -> c.id.equals("defend")).count());
        assertEquals(1, deck.stream().filter(c -> c.id.equals("bash")).count());
    }

    @Test
    void strikeDealsSixDamage() {
        Creature enemy = new Creature("e", 30);
        ActionManager mgr = combat(enemy);
        CardLibrary.newCard(CardId.STRIKE).use(enemy, mgr);
        mgr.runToCompletion();
        assertEquals(24, enemy.currentHp);
    }

    @Test
    void upgradedStrikeDealsNineDamage() {
        Creature enemy = new Creature("e", 30);
        ActionManager mgr = combat(enemy);
        AbstractCard strike = CardLibrary.newCard(CardId.STRIKE);
        strike.upgrade();
        strike.use(enemy, mgr);
        mgr.runToCompletion();
        assertEquals(21, enemy.currentHp);
    }

    @Test
    void defendGivesFiveBlock() {
        ActionManager mgr = combat(null);
        CardLibrary.newCard(CardId.DEFEND).use(null, mgr);
        mgr.runToCompletion();
        assertEquals(5, mgr.state().player.block);
    }

    @Test
    void bashDealsDamageAndAppliesVulnerable() {
        Creature enemy = new Creature("e", 30);
        ActionManager mgr = combat(enemy);
        CardLibrary.newCard(CardId.BASH).use(enemy, mgr);
        mgr.runToCompletion();
        assertEquals(22, enemy.currentHp); // 8 damage
        VulnerablePower v = enemy.getPower(VulnerablePower.class);
        assertEquals(2, v == null ? 0 : v.amount);
    }
}

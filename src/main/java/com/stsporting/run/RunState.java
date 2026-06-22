package com.stsporting.run;

import com.stsporting.content.cards.CardId;
import com.stsporting.content.cards.CardLibrary;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistent player state carried across a whole run: HP, gold, the master deck
 * (as card ids), and position (act/floor). Mutations are clamped so values stay
 * valid. Relics/potions will be added when that content exists.
 */
public class RunState {
    public static final int STARTING_MAX_HP = 80;
    public static final int STARTING_GOLD = 99;

    public int maxHp = STARTING_MAX_HP;
    public int currentHp = STARTING_MAX_HP;
    public int gold = STARTING_GOLD;
    public final List<CardId> masterDeck = new ArrayList<>();
    public int act = 1;
    public int floor = 0;
    public long seed;

    public RunState() {
    }

    /** Fresh Ironclad run from a seed: starter deck, full HP, starting gold. */
    public static RunState newRun(long seed) {
        RunState rs = new RunState();
        rs.seed = seed;
        rs.masterDeck.addAll(CardLibrary.starterDeckIds());
        return rs;
    }

    public boolean isDead() {
        return currentHp <= 0;
    }

    public void heal(int amount) {
        if (amount <= 0 || isDead()) {
            return;
        }
        currentHp = Math.min(maxHp, currentHp + amount);
    }

    public void loseHp(int amount) {
        if (amount <= 0) {
            return;
        }
        currentHp = Math.max(0, currentHp - amount);
    }

    public void increaseMaxHp(int amount) {
        if (amount <= 0) {
            return;
        }
        maxHp += amount;
        currentHp += amount;
    }

    public void addGold(int amount) {
        if (amount > 0) {
            gold += amount;
        }
    }

    public boolean spendGold(int amount) {
        if (amount < 0 || gold < amount) {
            return false;
        }
        gold -= amount;
        return true;
    }

    public void addCard(CardId id) {
        masterDeck.add(id);
    }

    public boolean removeCard(CardId id) {
        return masterDeck.remove(id);
    }
}

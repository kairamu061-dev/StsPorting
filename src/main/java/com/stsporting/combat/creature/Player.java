package com.stsporting.combat.creature;

/**
 * The player creature within a combat. Persistent run state (deck, relics,
 * potions) lives in the run layer; this holds the in-combat HP/block/powers.
 */
public class Player extends Creature {
    public Player() {
        super("Ironclad", 80);
    }

    public Player(int maxHp, int currentHp) {
        super("Ironclad", maxHp);
        this.currentHp = currentHp;
    }
}

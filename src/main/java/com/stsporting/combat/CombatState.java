package com.stsporting.combat;

import com.stsporting.combat.card.AbstractCard;
import com.stsporting.combat.creature.Creature;
import com.stsporting.combat.creature.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Mutable state of a single combat: the player, the live enemies, energy,
 * turn counter and the card piles. Turn phase is added by the turn-flow
 * sub-item. Pure state, mutated by {@code GameAction}s.
 */
public class CombatState {
    public static final int MAX_HAND = 10;

    public Player player;
    public final List<Creature> enemies = new ArrayList<>();
    public int energy;
    public int turn;

    // Card piles. "Top" of the draw pile is the last element.
    public final List<AbstractCard> drawPile = new ArrayList<>();
    public final List<AbstractCard> hand = new ArrayList<>();
    public final List<AbstractCard> discardPile = new ArrayList<>();
    public final List<AbstractCard> exhaustPile = new ArrayList<>();

    /** Deterministic RNG for shuffling (seeded from the run in real play). */
    public Random rng = new Random(0);

    public CombatState() {
    }

    public CombatState(Player player) {
        this.player = player;
    }

    public boolean allEnemiesDead() {
        for (Creature e : enemies) {
            if (!e.isDead()) {
                return false;
            }
        }
        return true;
    }

    public boolean isPlayerDead() {
        return player != null && player.isDead();
    }
}

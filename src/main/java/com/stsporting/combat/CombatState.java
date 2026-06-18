package com.stsporting.combat;

import com.stsporting.combat.creature.Creature;
import com.stsporting.combat.creature.Player;
import java.util.ArrayList;
import java.util.List;

/**
 * Mutable state of a single combat: the player, the live enemies, energy and
 * turn counter. Card piles and turn phase are added by the cards / turn-flow
 * sub-items. Pure state, mutated by {@code GameAction}s.
 */
public class CombatState {
    public Player player;
    public final List<Creature> enemies = new ArrayList<>();
    public int energy;
    public int turn;

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

package com.stsporting.combat.creature.enemy;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Weighted random move selection with consecutive-use limits, matching the
 * original's "can't repeat the same move too many times in a row" behaviour.
 * Deterministic for a given RNG and history.
 */
public final class MoveSelector {
    private MoveSelector() {
    }

    public static EnemyMove select(List<EnemyMove> moves, Deque<String> history, Random rng) {
        List<EnemyMove> available = new ArrayList<>();
        for (EnemyMove m : moves) {
            if (!exceedsConsecutive(m, history)) {
                available.add(m);
            }
        }
        if (available.isEmpty()) {
            available = new ArrayList<>(moves); // fallback: relax the limit
        }

        int total = 0;
        for (EnemyMove m : available) {
            total += Math.max(1, m.weight);
        }
        int roll = rng.nextInt(total);
        int acc = 0;
        for (EnemyMove m : available) {
            acc += Math.max(1, m.weight);
            if (roll < acc) {
                return m;
            }
        }
        return available.get(available.size() - 1);
    }

    /** True if the move id already occupies the last maxConsecutive history slots. */
    static boolean exceedsConsecutive(EnemyMove move, Deque<String> history) {
        if (move.maxConsecutive <= 0) {
            return false;
        }
        int count = 0;
        Iterator<String> it = history.descendingIterator();
        while (it.hasNext() && it.next().equals(move.id)) {
            count++;
        }
        return count >= move.maxConsecutive;
    }
}

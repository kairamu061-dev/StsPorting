package com.stsporting.content.monsters;

import com.stsporting.combat.creature.enemy.AbstractMonster;
import com.stsporting.combat.creature.enemy.EnemyMove;
import com.stsporting.combat.creature.enemy.Moves;
import com.stsporting.combat.power.RitualPower;
import java.util.List;
import java.util.Random;

/**
 * Cultist: casts Ritual on its first turn (gaining Strength each turn after),
 * then attacks every turn. A simple fixed-pattern AI.
 */
public class Cultist extends AbstractMonster {
    public Cultist() {
        super("cultist", "Cultist", 48, 54);
    }

    @Override
    public List<EnemyMove> moves() {
        return List.of(
                Moves.buff("ritual", RitualPower::new, 3, 1, 0),
                Moves.attack("dark_strike", 6, 1, 0));
    }

    @Override
    public void rollNextMove(Random rng) {
        setNextMove(moveHistory.isEmpty() ? findMove("ritual") : findMove("dark_strike"));
    }
}

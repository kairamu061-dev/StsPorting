package com.stsporting.combat.creature.enemy;

import com.stsporting.combat.DamageType;
import com.stsporting.combat.action.ApplyPowerAction;
import com.stsporting.combat.action.DamageAction;
import com.stsporting.combat.action.GainBlockAction;
import com.stsporting.combat.power.AbstractPower;
import java.util.function.Supplier;

/** Factory helpers for common enemy moves. */
public final class Moves {
    private Moves() {
    }

    public static EnemyMove attack(String id, int damage, int weight, int maxConsecutive) {
        return new EnemyMove(id, IntentType.ATTACK, damage, 1, weight, maxConsecutive,
                (mgr, self, player) ->
                        mgr.addToBottom(new DamageAction(player, damage, self, DamageType.ATTACK)));
    }

    public static EnemyMove attackMulti(String id, int damage, int hits, int weight, int maxConsecutive) {
        return new EnemyMove(id, IntentType.ATTACK_MULTI, damage, hits, weight, maxConsecutive,
                (mgr, self, player) -> {
                    for (int i = 0; i < hits; i++) {
                        mgr.addToBottom(new DamageAction(player, damage, self, DamageType.ATTACK));
                    }
                });
    }

    public static EnemyMove defend(String id, int block, int weight, int maxConsecutive) {
        return new EnemyMove(id, IntentType.DEFEND, 0, 0, weight, maxConsecutive,
                (mgr, self, player) -> mgr.addToBottom(new GainBlockAction(self, block)));
    }

    /** Self-buff: applies a power to the enemy itself. */
    public static EnemyMove buff(String id, Supplier<AbstractPower> power, int amount,
                                 int weight, int maxConsecutive) {
        return new EnemyMove(id, IntentType.BUFF, 0, 0, weight, maxConsecutive,
                (mgr, self, player) -> mgr.addToBottom(new ApplyPowerAction(self, power.get(), amount)));
    }

    /** Debuff: applies a power to the player. */
    public static EnemyMove debuff(String id, Supplier<AbstractPower> power, int amount,
                                   int weight, int maxConsecutive) {
        return new EnemyMove(id, IntentType.DEBUFF, 0, 0, weight, maxConsecutive,
                (mgr, self, player) -> mgr.addToBottom(new ApplyPowerAction(player, power.get(), amount)));
    }
}

package com.stsporting.combat.creature.enemy;

import com.stsporting.combat.action.ActionManager;
import com.stsporting.combat.creature.Player;

/**
 * One possible enemy action: its intent classification, attack values (if any),
 * selection weight, a consecutive-use cap (0 = unlimited), and the effect to
 * enqueue when executed.
 */
public class EnemyMove {
    public final String id;
    public final IntentType type;
    public final int baseDamage;
    public final int hits;
    public final int weight;
    public final int maxConsecutive;
    private final MoveEffect effect;

    public EnemyMove(String id, IntentType type, int baseDamage, int hits,
                     int weight, int maxConsecutive, MoveEffect effect) {
        this.id = id;
        this.type = type;
        this.baseDamage = baseDamage;
        this.hits = hits;
        this.weight = weight;
        this.maxConsecutive = maxConsecutive;
        this.effect = effect;
    }

    public void enqueue(ActionManager mgr, AbstractMonster self, Player player) {
        effect.enqueue(mgr, self, player);
    }
}

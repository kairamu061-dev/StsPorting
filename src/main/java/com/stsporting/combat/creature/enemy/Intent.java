package com.stsporting.combat.creature.enemy;

/**
 * Display model for a telegraphed enemy action. For attacks, {@code damage} is
 * the per-hit value the player will actually take (player powers already
 * applied) and {@code hits} the number of strikes.
 */
public class Intent {
    public final IntentType type;
    public final int damage;
    public final int hits;

    public Intent(IntentType type, int damage, int hits) {
        this.type = type;
        this.damage = damage;
        this.hits = hits;
    }

    public static Intent nonAttack(IntentType type) {
        return new Intent(type, 0, 0);
    }
}

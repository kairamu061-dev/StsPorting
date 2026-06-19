package com.stsporting.combat.creature.enemy;

import com.stsporting.combat.DamageType;
import com.stsporting.combat.action.ActionManager;
import com.stsporting.combat.creature.Creature;
import com.stsporting.combat.creature.Player;
import com.stsporting.combat.power.PowerHooks;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Random;

/**
 * Base enemy: a {@link Creature} with an HP range, a telegraphed next move and
 * a move history used to enforce consecutive-use limits. Concrete enemies
 * supply {@link #moves()} and may override {@link #rollNextMove} for fixed or
 * stateful patterns.
 */
public abstract class AbstractMonster extends Creature {
    public final String monsterId;
    protected EnemyMove nextMove;
    protected final Deque<String> moveHistory = new ArrayDeque<>();
    protected final int hpMin;
    protected final int hpMax;

    protected AbstractMonster(String monsterId, String name, int hpMin, int hpMax) {
        super(name, hpMax);
        this.monsterId = monsterId;
        this.hpMin = hpMin;
        this.hpMax = hpMax;
    }

    /** Roll HP within range and pick the first move. Deterministic per RNG. */
    public void initialize(Random rng) {
        int range = hpMax - hpMin;
        this.maxHp = hpMin + (range > 0 ? rng.nextInt(range + 1) : 0);
        this.currentHp = maxHp;
        rollNextMove(rng);
    }

    public abstract List<EnemyMove> moves();

    /** Default: weighted selection honouring consecutive limits. */
    public void rollNextMove(Random rng) {
        setNextMove(MoveSelector.select(moves(), moveHistory, rng));
    }

    /** Execute the telegraphed move and record it in history. */
    public void takeTurn(ActionManager mgr, Player player) {
        if (nextMove != null) {
            nextMove.enqueue(mgr, this, player);
            moveHistory.addLast(nextMove.id);
        }
    }

    public EnemyMove nextMove() {
        return nextMove;
    }

    protected void setNextMove(EnemyMove move) {
        this.nextMove = move;
    }

    protected EnemyMove findMove(String id) {
        for (EnemyMove m : moves()) {
            if (m.id.equals(id)) {
                return m;
            }
        }
        return null;
    }

    /** Telegraph for the upcoming move; attack preview reflects player powers. */
    public Intent getIntent(Player player) {
        if (nextMove == null) {
            return Intent.nonAttack(IntentType.UNKNOWN);
        }
        if (nextMove.type.isAttack()) {
            return new Intent(nextMove.type, previewDamage(nextMove.baseDamage, player), nextMove.hits);
        }
        return Intent.nonAttack(nextMove.type);
    }

    protected int previewDamage(int base, Player player) {
        int d = PowerHooks.applyDamageGive(this, base, DamageType.ATTACK);
        d = PowerHooks.applyDamageReceive(player, d, DamageType.ATTACK);
        return Math.max(0, d);
    }
}

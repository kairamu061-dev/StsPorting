package com.stsporting.combat.flow;

import com.stsporting.combat.CombatState;
import com.stsporting.combat.action.ActionManager;
import com.stsporting.combat.card.AbstractCard;
import com.stsporting.combat.card.DiscardAction;
import com.stsporting.combat.card.DrawAction;
import com.stsporting.combat.creature.Creature;
import com.stsporting.combat.power.AbstractPower;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Drives the combat phase machine: BEGIN -> PLAYER -> END_PLAYER -> ENEMY ->
 * (loop) -> FINISHED. Phase transitions only happen when the action queue is
 * idle, so nothing advances mid-animation. All turn-boundary work (block reset,
 * power hooks, draw, decay) is funnelled through the action queue / state in a
 * fixed order to reproduce the original's resolution.
 */
public class TurnController {
    public enum Phase {
        BEGIN, PLAYER, END_PLAYER, ENEMY, FINISHED
    }

    public static final int INITIAL_DRAW = 5;

    private final CombatState state;
    private final ActionManager mgr;
    private final EnergyManager energy;
    private Phase phase = Phase.BEGIN;
    private boolean endTurnRequested;

    public TurnController(CombatState state, ActionManager mgr) {
        this.state = state;
        this.mgr = mgr;
        this.energy = new EnergyManager(state);
    }

    public Phase phase() {
        return phase;
    }

    public EnergyManager energy() {
        return energy;
    }

    public void requestEndTurn() {
        if (phase == Phase.PLAYER) {
            endTurnRequested = true;
        }
    }

    public boolean isPlayerInputAllowed() {
        return phase == Phase.PLAYER && mgr.isIdle() && !endTurnRequested;
    }

    public void update(float delta) {
        mgr.update(delta);
        if (!mgr.isIdle()) {
            return;
        }
        if (phase != Phase.FINISHED && (state.isPlayerDead() || state.allEnemiesDead())) {
            phase = Phase.FINISHED;
            return;
        }
        switch (phase) {
            case BEGIN:
                startCombat();
                phase = Phase.PLAYER;
                break;
            case PLAYER:
                if (endTurnRequested) {
                    endTurnRequested = false;
                    endPlayerTurn();
                    phase = Phase.END_PLAYER;
                }
                break;
            case END_PLAYER:
                enemyTurn();
                phase = Phase.ENEMY;
                break;
            case ENEMY:
                startPlayerTurn();
                phase = Phase.PLAYER;
                break;
            case FINISHED:
            default:
                break;
        }
    }

    private void startCombat() {
        Collections.shuffle(state.drawPile, state.rng);
        // (relic atBattleStart hooks would fire here once relics are wired)
        startPlayerTurn();
    }

    private void startPlayerTurn() {
        state.turn++;
        if (state.player != null) {
            state.player.block = 0;
            for (AbstractPower p : new ArrayList<>(state.player.powers)) {
                p.atStartOfTurn();
            }
        }
        energy.refill();
        for (AbstractCard c : state.hand) {
            c.resetCostForTurn();
        }
        mgr.addToBottom(new DrawAction(INITIAL_DRAW));
    }

    private void endPlayerTurn() {
        if (state.player != null) {
            for (AbstractPower p : new ArrayList<>(state.player.powers)) {
                p.atEndOfTurn(true);
            }
            reduceDebuffsAndPrune(state.player);
        }
        // Discard the remaining hand (no retain handling yet).
        for (AbstractCard c : new ArrayList<>(state.hand)) {
            mgr.addToBottom(new DiscardAction(c));
        }
    }

    private void enemyTurn() {
        for (Creature enemy : new ArrayList<>(state.enemies)) {
            if (enemy.isDead()) {
                continue;
            }
            enemy.block = 0;
            for (AbstractPower p : new ArrayList<>(enemy.powers)) {
                p.atStartOfTurn();
            }
            if (enemy instanceof com.stsporting.combat.creature.enemy.AbstractMonster) {
                com.stsporting.combat.creature.enemy.AbstractMonster m =
                        (com.stsporting.combat.creature.enemy.AbstractMonster) enemy;
                m.takeTurn(mgr, state.player);
                for (AbstractPower p : new ArrayList<>(enemy.powers)) {
                    p.atEndOfTurn(false);
                }
                reduceDebuffsAndPrune(enemy);
                m.rollNextMove(state.rng);
            }
        }
    }

    private void reduceDebuffsAndPrune(Creature c) {
        for (AbstractPower p : new ArrayList<>(c.powers)) {
            p.reducePerTurn();
            if (p.shouldRemove()) {
                c.powers.remove(p);
            }
        }
    }
}

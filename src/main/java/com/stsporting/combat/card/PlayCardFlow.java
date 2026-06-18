package com.stsporting.combat.card;

import com.stsporting.combat.CombatState;
import com.stsporting.combat.action.ActionManager;
import com.stsporting.combat.creature.Creature;

/**
 * Resolves a play request: validates (playable / energy / target), spends
 * energy, removes the card from hand, runs its effects via {@code use}, and
 * queues a {@link PostPlayAction} to move it once effects resolve.
 *
 * <p>Energy is spent against {@code CombatState.energy} directly for now; the
 * turn-flow sub-item's EnergyManager will own this when it lands.
 */
public final class PlayCardFlow {
    private PlayCardFlow() {
    }

    /** @return true if the card was played; false if the play was rejected. */
    public static boolean resolve(ActionManager mgr, AbstractCard card, Creature target) {
        CombatState s = mgr.state();
        if (!card.isPlayable()) {
            return false;
        }
        if (card.target == CardTarget.ENEMY && (target == null || target.isDead())) {
            return false;
        }
        int cost = card.cost();
        if (s.energy < cost) {
            return false;
        }

        s.energy -= cost;
        card.freeToPlayOnce = false;
        s.hand.remove(card);

        card.use(target, mgr);
        mgr.addToBottom(new PostPlayAction(card));
        return true;
    }

    public static boolean resolve(ActionManager mgr, CardQueueItem item) {
        return resolve(mgr, item.card, item.target);
    }
}

package com.stsporting.combat.creature.enemy;

import com.stsporting.combat.action.ActionManager;
import com.stsporting.combat.creature.Player;

/** How an enemy move enqueues its concrete effect actions when executed. */
@FunctionalInterface
public interface MoveEffect {
    void enqueue(ActionManager mgr, AbstractMonster self, Player player);
}

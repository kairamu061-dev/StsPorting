package com.stsporting.combat.creature.enemy;

/** The kind of action an enemy telegraphs for its next turn. */
public enum IntentType {
    ATTACK,
    ATTACK_MULTI,
    DEFEND,
    BUFF,
    DEBUFF,
    ATTACK_DEFEND,
    UNKNOWN;

    public boolean isAttack() {
        return this == ATTACK || this == ATTACK_MULTI || this == ATTACK_DEFEND;
    }
}

package com.stsporting.combat.card;

/** What a card targets when played. */
public enum CardTarget {
    /** A single chosen enemy. */
    ENEMY,
    /** All enemies. */
    ALL_ENEMY,
    /** The player. */
    SELF,
    /** No target (e.g. pure draw/energy). */
    NONE
}

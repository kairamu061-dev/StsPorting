package com.stsporting.combat;

/**
 * Classifies a source of damage. Only {@link #ATTACK} is affected by attack
 * modifiers (Strength/Weak) and target Vulnerable; {@link #THORNS} and
 * {@link #HP_LOSS} are flat and bypass those modifiers.
 */
public enum DamageType {
    /** Card/enemy attack damage. Subject to Strength/Weak/Vulnerable. */
    ATTACK,
    /** Reflected damage (e.g. Thorns). Flat, hits HP through block? No — uses block too. */
    THORNS,
    /** Direct HP loss (e.g. Poison). Bypasses block; not modified. */
    HP_LOSS
}

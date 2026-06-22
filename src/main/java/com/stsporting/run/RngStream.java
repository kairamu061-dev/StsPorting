package com.stsporting.run;

/** Independent deterministic RNG streams so unrelated rolls don't interfere. */
public enum RngStream {
    MAP,
    MONSTER,
    CARD_REWARD,
    EVENT,
    TREASURE,
    MISC
}

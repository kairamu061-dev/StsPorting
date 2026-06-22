package com.stsporting.run;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class RunRngTest {

    @Test
    void sameSeedProducesSameSequence() {
        RunRng a = new RunRng(42);
        RunRng b = new RunRng(42);
        for (int i = 0; i < 20; i++) {
            assertEquals(a.stream(RngStream.MAP).nextInt(1000),
                    b.stream(RngStream.MAP).nextInt(1000));
        }
    }

    @Test
    void streamsAreIndependent() {
        RunRng r = new RunRng(7);
        // Consuming MAP must not change MONSTER's sequence relative to a fresh rng.
        RunRng ref = new RunRng(7);
        r.stream(RngStream.MAP).nextInt();
        r.stream(RngStream.MAP).nextInt();
        assertEquals(ref.stream(RngStream.MONSTER).nextLong(),
                r.stream(RngStream.MONSTER).nextLong());
    }

    @Test
    void differentSeedsDiffer() {
        RunRng a = new RunRng(1);
        RunRng b = new RunRng(2);
        assertNotEquals(a.stream(RngStream.MAP).nextLong(),
                b.stream(RngStream.MAP).nextLong());
    }
}

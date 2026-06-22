package com.stsporting.run;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

/**
 * Per-stream deterministic RNG derived from a single run seed. The same seed
 * always yields the same map/encounters/rewards, and each stream advances
 * independently so consuming one doesn't shift another.
 */
public class RunRng {
    private final long seed;
    private final Map<RngStream, Random> streams = new EnumMap<>(RngStream.class);

    public RunRng(long seed) {
        this.seed = seed;
        for (RngStream s : RngStream.values()) {
            // Offset each stream's seed so they're decorrelated but deterministic.
            streams.put(s, new Random(seed + 0x9E3779B97F4A7C15L * (s.ordinal() + 1)));
        }
    }

    public long seed() {
        return seed;
    }

    public Random stream(RngStream which) {
        return streams.get(which);
    }
}

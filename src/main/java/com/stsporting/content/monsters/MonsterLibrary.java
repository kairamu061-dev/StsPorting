package com.stsporting.content.monsters;

import com.stsporting.combat.creature.enemy.AbstractMonster;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

/** Registry of enemy factories. Callers initialize the returned monster. */
public final class MonsterLibrary {
    private static final Map<MonsterId, Supplier<AbstractMonster>> FACTORIES =
            new EnumMap<>(MonsterId.class);

    static {
        FACTORIES.put(MonsterId.CULTIST, Cultist::new);
        FACTORIES.put(MonsterId.JAW_WORM, JawWorm::new);
    }

    private MonsterLibrary() {
    }

    public static AbstractMonster newMonster(MonsterId id) {
        Supplier<AbstractMonster> f = FACTORIES.get(id);
        if (f == null) {
            throw new IllegalArgumentException("Unregistered monster: " + id);
        }
        return f.get();
    }
}

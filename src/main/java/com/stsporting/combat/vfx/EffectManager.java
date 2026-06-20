package com.stsporting.combat.vfx;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.List;

/**
 * Owns the live visual effects: updates them, prunes finished ones, and renders
 * them. Capped to avoid unbounded growth on heavy turns.
 */
public class EffectManager {
    private static final int MAX_EFFECTS = 256;

    private final List<AbstractEffect> effects = new ArrayList<>();

    public void add(AbstractEffect effect) {
        if (effects.size() >= MAX_EFFECTS) {
            effects.remove(0); // drop the oldest
        }
        effects.add(effect);
    }

    public void update(float delta) {
        for (int i = effects.size() - 1; i >= 0; i--) {
            AbstractEffect e = effects.get(i);
            e.update(delta);
            if (e.isDone) {
                effects.remove(i);
            }
        }
    }

    public void render(SpriteBatch batch, BitmapFont font) {
        for (AbstractEffect e : effects) {
            e.render(batch, font);
        }
    }

    public int size() {
        return effects.size();
    }
}

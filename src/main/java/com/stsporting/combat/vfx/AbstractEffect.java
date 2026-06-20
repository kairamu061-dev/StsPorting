package com.stsporting.combat.vfx;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * A time-bounded visual effect. {@link #update} advances elapsed time and flags
 * {@link #isDone}; {@link #progress()} (0..1) drives interpolation. Lifecycle is
 * GL-free and unit-testable; only {@link #render} touches graphics.
 */
public abstract class AbstractEffect {
    protected final float duration;
    protected float elapsed;
    public boolean isDone;

    protected AbstractEffect(float duration) {
        this.duration = duration;
    }

    public void update(float delta) {
        elapsed += delta;
        if (elapsed >= duration) {
            isDone = true;
        }
    }

    public float progress() {
        return duration <= 0f ? 1f : Math.min(1f, elapsed / duration);
    }

    public abstract void render(SpriteBatch batch, BitmapFont font);
}

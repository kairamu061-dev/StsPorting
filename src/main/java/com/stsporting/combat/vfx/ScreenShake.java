package com.stsporting.combat.vfx;

import java.util.Random;

/**
 * Camera shake with linear decay. Intensity ramps down to zero over the shake
 * window; {@link #offsetX()}/{@link #offsetY()} give the per-frame jitter to add
 * to the camera position. Decay math is GL-free and unit-testable.
 */
public class ScreenShake {
    private final Random rng = new Random();
    private float intensity;
    private float duration;
    private float timeLeft;

    /** Start (or strengthen) a shake. Stacks by taking the stronger intensity. */
    public void shake(float intensity, float duration) {
        this.intensity = Math.max(this.intensity, intensity);
        this.duration = duration;
        this.timeLeft = duration;
    }

    public void update(float delta) {
        if (timeLeft > 0f) {
            timeLeft = Math.max(0f, timeLeft - delta);
            if (timeLeft == 0f) {
                intensity = 0f;
            }
        }
    }

    public boolean active() {
        return timeLeft > 0f;
    }

    public float currentIntensity() {
        return duration <= 0f ? 0f : intensity * (timeLeft / duration);
    }

    public float offsetX() {
        return active() ? (rng.nextFloat() * 2f - 1f) * currentIntensity() : 0f;
    }

    public float offsetY() {
        return active() ? (rng.nextFloat() * 2f - 1f) * currentIntensity() : 0f;
    }
}

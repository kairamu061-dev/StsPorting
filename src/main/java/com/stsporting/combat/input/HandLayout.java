package com.stsporting.combat.input;

import com.badlogic.gdx.math.Rectangle;

/**
 * Computes the fanned, arc-shaped positions of cards in hand. Pure geometry so
 * it can be unit-tested without a GL context. The card index runs left to right;
 * the middle of the hand lifts highest and the cards rotate from a slight
 * left-lean to a slight right-lean.
 */
public class HandLayout {
    public float centerX;
    public float baseY;        // vertical centre of an un-lifted card
    public float cardW;
    public float cardH;
    public float perCardSpacing;
    public float maxHandWidth;
    public float arcHeight;     // extra lift at the middle of the fan
    public float maxAngleDeg;   // tilt of the outermost cards

    /** Default layout tuned for the 1920x1080 virtual space. */
    public HandLayout() {
        this(960f, 200f, 200f, 280f, 190f, 1150f, 40f, 10f);
    }

    public HandLayout(float centerX, float baseY, float cardW, float cardH,
                      float perCardSpacing, float maxHandWidth, float arcHeight, float maxAngleDeg) {
        this.centerX = centerX;
        this.baseY = baseY;
        this.cardW = cardW;
        this.cardH = cardH;
        this.perCardSpacing = perCardSpacing;
        this.maxHandWidth = maxHandWidth;
        this.arcHeight = arcHeight;
        this.maxAngleDeg = maxAngleDeg;
    }

    /** Centre position/tilt of card {@code index} in a hand of {@code count}. */
    public Pose poseFor(int index, int count) {
        float t = (count <= 1) ? 0.5f : index / (float) (count - 1);
        float spread = Math.min(maxHandWidth, count * perCardSpacing);
        float x = centerX + (t - 0.5f) * spread;
        float y = baseY + (float) Math.sin(t * Math.PI) * arcHeight;
        float angle = lerp(maxAngleDeg, -maxAngleDeg, t);
        return new Pose(x, y, angle);
    }

    /** Axis-aligned hit rectangle (rotation ignored for picking). */
    public Rectangle hitRect(int index, int count) {
        Pose p = poseFor(index, count);
        return new Rectangle(p.x - cardW / 2f, p.y - cardH / 2f, cardW, cardH);
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
}

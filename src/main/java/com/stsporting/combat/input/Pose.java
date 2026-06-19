package com.stsporting.combat.input;

/** A card's render placement: centre position and tilt (degrees). */
public class Pose {
    public final float x;
    public final float y;
    public final float angle;

    public Pose(float x, float y, float angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }
}

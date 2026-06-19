package com.stsporting.combat.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.math.Rectangle;
import org.junit.jupiter.api.Test;

class HandLayoutTest {

    /** Flat layout (no arc/tilt) for exact geometry assertions. */
    private HandLayout flat() {
        return new HandLayout(1000f, 200f, 100f, 200f, 120f, 1000f, 0f, 0f);
    }

    @Test
    void singleCardIsCentered() {
        Pose p = flat().poseFor(0, 1);
        assertEquals(1000f, p.x, 0.001f);
        assertEquals(200f, p.y, 0.001f);
    }

    @Test
    void twoCardsSpreadSymmetrically() {
        HandLayout l = flat();
        Pose left = l.poseFor(0, 2);
        Pose right = l.poseFor(1, 2);
        // spread = min(1000, 2*120) = 240; halves to +-120 around centre.
        assertEquals(880f, left.x, 0.001f);
        assertEquals(1120f, right.x, 0.001f);
    }

    @Test
    void hitRectContainsPoseCentre() {
        HandLayout l = flat();
        Rectangle r = l.hitRect(2, 5);
        Pose p = l.poseFor(2, 5);
        assertTrue(r.contains(p.x, p.y));
    }

    @Test
    void middleCardLiftsWithArc() {
        HandLayout arced = new HandLayout(1000f, 200f, 100f, 200f, 120f, 1000f, 40f, 0f);
        float endY = arced.poseFor(0, 3).y;
        float midY = arced.poseFor(1, 3).y;
        assertTrue(midY > endY, "middle card should be lifted above the ends");
    }
}

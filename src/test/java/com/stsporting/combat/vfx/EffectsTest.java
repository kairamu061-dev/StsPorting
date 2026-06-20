package com.stsporting.combat.vfx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.junit.jupiter.api.Test;

/** GL-free lifecycle/maths tests for the effects layer. */
class EffectsTest {

    /** A render-free effect so EffectManager can be tested headlessly. */
    private static final class Dummy extends AbstractEffect {
        Dummy(float d) {
            super(d);
        }

        @Override
        public void render(SpriteBatch batch, BitmapFont font) {
        }
    }

    @Test
    void effectFinishesAfterItsDuration() {
        Dummy e = new Dummy(0.5f);
        e.update(0.2f);
        assertFalse(e.isDone);
        assertEquals(0.4f, e.progress(), 0.001f);
        e.update(0.4f); // total 0.6 >= 0.5
        assertTrue(e.isDone);
        assertEquals(1f, e.progress(), 0.001f);
    }

    @Test
    void managerPrunesFinishedEffects() {
        EffectManager m = new EffectManager();
        m.add(new Dummy(0.3f));
        m.add(new Dummy(1.0f));
        assertEquals(2, m.size());
        m.update(0.5f); // first finishes
        assertEquals(1, m.size());
        m.update(0.6f); // second finishes
        assertEquals(0, m.size());
    }

    @Test
    void damageNumberRisesAndFades() {
        DamageNumberEffect n = new DamageNumberEffect(100, 200, 12, Color.SCARLET, 2f);
        float y0 = n.currentY();
        float a0 = n.alpha();
        n.update(0.4f); // half of 0.8s
        assertTrue(n.currentY() > y0, "should rise");
        assertTrue(n.alpha() < a0, "should fade");
    }

    @Test
    void screenShakeDecaysToZero() {
        ScreenShake s = new ScreenShake();
        s.shake(20f, 0.5f);
        assertTrue(s.active());
        assertTrue(s.currentIntensity() > 0f);
        s.update(0.5f);
        assertFalse(s.active());
        assertEquals(0f, s.currentIntensity(), 0.001f);
        assertEquals(0f, s.offsetX(), 0.001f);
    }
}

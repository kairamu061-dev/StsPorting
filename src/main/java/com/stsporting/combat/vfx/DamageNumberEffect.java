package com.stsporting.combat.vfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * A number that floats upward and fades out (damage in red, block in blue, etc).
 * Position interpolation/alpha are exposed for testing without rendering.
 */
public class DamageNumberEffect extends AbstractEffect {
    private static final float RISE = 90f;

    private final float x;
    private final float baseY;
    private final String text;
    private final Color color;
    private final float scale;

    public DamageNumberEffect(float x, float y, int amount, Color color, float scale) {
        super(0.8f);
        this.x = x;
        this.baseY = y;
        this.text = Integer.toString(amount);
        this.color = color;
        this.scale = scale;
    }

    public float currentY() {
        return baseY + progress() * RISE;
    }

    public float alpha() {
        return 1f - progress();
    }

    @Override
    public void render(SpriteBatch batch, BitmapFont font) {
        font.getData().setScale(scale);
        font.setColor(color.r, color.g, color.b, alpha());
        font.draw(batch, text, x, currentY());
        font.setColor(Color.WHITE);
    }
}

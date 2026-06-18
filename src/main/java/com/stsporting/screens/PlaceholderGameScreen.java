package com.stsporting.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.stsporting.core.GameContext;
import com.stsporting.core.GameScreen;
import com.stsporting.core.InputConsumer;
import com.stsporting.render.ViewportConfig;

/**
 * Stand-in for the in-game screen. Later feature areas (run/map/combat) replace
 * this; for now it just proves the New Run transition and returns to the menu
 * on ESC. The single hand-off point is {@code MainMenuScreen}'s "New Run".
 */
public class PlaceholderGameScreen implements GameScreen, InputConsumer {
    private final GameContext ctx;
    private final GlyphLayout layout = new GlyphLayout();

    public PlaceholderGameScreen(GameContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        BitmapFont font = ctx.assets.defaultFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2.5f);
        ctx.batch.begin();
        layout.setText(font, "Game Screen (placeholder) - press ESC for menu");
        font.draw(ctx.batch, layout,
                (ViewportConfig.VIRTUAL_WIDTH - layout.width) / 2f,
                ViewportConfig.VIRTUAL_HEIGHT / 2f);
        ctx.batch.end();
    }

    @Override
    public boolean onKeyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            ctx.screens.replace(new MainMenuScreen(ctx));
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchDown(float vx, float vy, int button) {
        return false;
    }

    @Override
    public boolean onTouchUp(float vx, float vy, int button) {
        return false;
    }

    @Override
    public boolean onMouseMoved(float vx, float vy) {
        return false;
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
}

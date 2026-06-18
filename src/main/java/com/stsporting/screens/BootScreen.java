package com.stsporting.screens;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.stsporting.core.GameContext;
import com.stsporting.core.GameScreen;
import com.stsporting.render.ViewportConfig;

/**
 * Startup screen: advances asset loading and shows progress, then hands off to
 * the main menu. With no bundled assets yet, this completes immediately.
 */
public class BootScreen implements GameScreen {
    private final GameContext ctx;
    private final GlyphLayout layout = new GlyphLayout();

    public BootScreen(GameContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void show() {
        ctx.assets.queueCommon();
    }

    @Override
    public void render(float delta) {
        boolean done = ctx.assets.update();

        BitmapFont font = ctx.assets.defaultFont();
        font.getData().setScale(2f);
        String text = "Loading... " + (int) (ctx.assets.progress() * 100) + "%";
        layout.setText(font, text);
        ctx.batch.begin();
        font.draw(ctx.batch, layout,
                (ViewportConfig.VIRTUAL_WIDTH - layout.width) / 2f,
                ViewportConfig.VIRTUAL_HEIGHT / 2f);
        ctx.batch.end();

        if (done) {
            ctx.screens.replace(new MainMenuScreen(ctx));
        }
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

package com.stsporting;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ScreenUtils;
import com.stsporting.core.GameContext;
import com.stsporting.core.InputRouter;
import com.stsporting.screens.BootScreen;

/**
 * Application entry point on the libGDX side. Builds the shared
 * {@link GameContext}, wires input, and drives the active screen each frame.
 */
public class StsGame extends ApplicationAdapter {
    private GameContext ctx;

    @Override
    public void create() {
        ctx = new GameContext();
        Gdx.input.setInputProcessor(new InputRouter(ctx));
        ctx.screens.replace(new BootScreen(ctx));
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        // Dark cavern-tone background.
        ScreenUtils.clear(0.10f, 0.078f, 0.063f, 1f);
        ctx.viewport.apply();
        ctx.batch.setProjectionMatrix(ctx.camera.combined);
        ctx.screens.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        if (ctx != null) {
            ctx.viewport.update(width, height, true);
            ctx.screens.resize(width, height);
        }
    }

    @Override
    public void dispose() {
        if (ctx != null) {
            ctx.dispose();
        }
    }
}

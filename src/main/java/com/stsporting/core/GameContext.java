package com.stsporting.core;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.stsporting.render.ViewportConfig;

/**
 * Shared dependencies handed to every screen: the sprite batch, virtual-
 * resolution viewport/camera, asset manager, screen stack and settings.
 * Constructed once in {@code StsGame.create()} (requires a GL context).
 */
public class GameContext implements Disposable {
    public final SpriteBatch batch;
    public final OrthographicCamera camera;
    public final Viewport viewport;
    public final Assets assets;
    public final ScreenManager screens;
    public final Settings settings;

    public GameContext() {
        this.batch = new SpriteBatch();
        this.camera = new OrthographicCamera();
        this.viewport = ViewportConfig.create(camera);
        this.assets = new Assets();
        this.screens = new ScreenManager();
        this.settings = new Settings();
    }

    @Override
    public void dispose() {
        screens.dispose();
        assets.dispose();
        batch.dispose();
    }
}

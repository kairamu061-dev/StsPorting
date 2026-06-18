package com.stsporting.render;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Virtual-resolution configuration. The whole game lays out against a fixed
 * 1920x1080 logical space (matching the original); a {@link FitViewport}
 * letterboxes to keep the aspect ratio on any real window size.
 */
public final class ViewportConfig {
    public static final float VIRTUAL_WIDTH = 1920f;
    public static final float VIRTUAL_HEIGHT = 1080f;

    private ViewportConfig() {
    }

    public static Viewport create(OrthographicCamera camera) {
        return new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
    }
}

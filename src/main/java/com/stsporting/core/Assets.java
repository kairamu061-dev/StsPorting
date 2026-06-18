package com.stsporting.core;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Disposable;

/**
 * Thin wrapper over libGDX {@link AssetManager}. Provides placeholder fallbacks
 * so missing assets degrade gracefully (magenta texture / default font) instead
 * of crashing — original assets are not bundled in this project.
 */
public class Assets implements Disposable {
    private final AssetManager manager = new AssetManager();
    private Texture placeholder;
    private BitmapFont defaultFont;

    /** Queue common assets for loading. No bundled assets yet. */
    public void queueCommon() {
        // Intentionally empty for now; placeholders are created lazily.
    }

    /** Advance async loading. Returns true when the queue is fully loaded. */
    public boolean update() {
        return manager.update();
    }

    public float progress() {
        return manager.getProgress();
    }

    /** Get a loaded asset, or a placeholder texture when missing. */
    @SuppressWarnings("unchecked")
    public <T> T get(String path, Class<T> type) {
        if (manager.isLoaded(path, type)) {
            return manager.get(path, type);
        }
        if (type == Texture.class) {
            return (T) placeholderTexture();
        }
        if (type == BitmapFont.class) {
            return (T) defaultFont();
        }
        throw new IllegalArgumentException("Asset not loaded and no placeholder for: " + path);
    }

    /** 1x1 magenta texture used in place of missing textures. */
    public Texture placeholderTexture() {
        if (placeholder == null) {
            Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pm.setColor(Color.MAGENTA);
            pm.fill();
            placeholder = new Texture(pm);
            pm.dispose();
        }
        return placeholder;
    }

    /** libGDX built-in font, used until a project font is bundled. */
    public BitmapFont defaultFont() {
        if (defaultFont == null) {
            defaultFont = new BitmapFont();
        }
        return defaultFont;
    }

    @Override
    public void dispose() {
        manager.dispose();
        if (placeholder != null) {
            placeholder.dispose();
        }
        if (defaultFont != null) {
            defaultFont.dispose();
        }
    }
}

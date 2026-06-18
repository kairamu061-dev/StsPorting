package com.stsporting.core;

/**
 * A scene managed by {@link ScreenManager}. Intentionally independent of
 * libGDX's own Screen so screen-stack logic stays unit-testable without a GL
 * context. Implement {@link InputConsumer} as well to receive input.
 */
public interface GameScreen {
    /** Called when the screen becomes the active top of the stack. */
    void show();

    /** Per-frame update + draw. delta is seconds since last frame. */
    void render(float delta);

    /** Window resized; width/height are in physical pixels. */
    void resize(int width, int height);

    /** Called when the screen is no longer the active top (covered or removed). */
    void hide();

    /** Release resources owned by this screen. */
    void dispose();
}

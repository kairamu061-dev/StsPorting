package com.stsporting.core;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;

/**
 * Single libGDX {@code InputProcessor} for the whole app. Translates physical
 * screen coordinates into virtual (world) coordinates via the viewport and
 * forwards events to the active screen when it implements {@link InputConsumer}.
 */
public class InputRouter extends InputAdapter {
    private final GameContext ctx;

    public InputRouter(GameContext ctx) {
        this.ctx = ctx;
    }

    private InputConsumer activeConsumer() {
        GameScreen current = ctx.screens.current();
        return (current instanceof InputConsumer) ? (InputConsumer) current : null;
    }

    private Vector2 toVirtual(int screenX, int screenY) {
        return ctx.viewport.unproject(new Vector2(screenX, screenY));
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        InputConsumer c = activeConsumer();
        if (c == null) {
            return false;
        }
        Vector2 v = toVirtual(screenX, screenY);
        return c.onTouchDown(v.x, v.y, button);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        InputConsumer c = activeConsumer();
        if (c == null) {
            return false;
        }
        Vector2 v = toVirtual(screenX, screenY);
        return c.onTouchUp(v.x, v.y, button);
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        InputConsumer c = activeConsumer();
        if (c == null) {
            return false;
        }
        Vector2 v = toVirtual(screenX, screenY);
        return c.onMouseMoved(v.x, v.y);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // Treat drags as moves so screens can track the pointer while held.
        InputConsumer c = activeConsumer();
        if (c == null) {
            return false;
        }
        Vector2 v = toVirtual(screenX, screenY);
        return c.onMouseMoved(v.x, v.y);
    }

    @Override
    public boolean keyDown(int keycode) {
        InputConsumer c = activeConsumer();
        if (c == null) {
            return false;
        }
        return c.onKeyDown(keycode);
    }
}

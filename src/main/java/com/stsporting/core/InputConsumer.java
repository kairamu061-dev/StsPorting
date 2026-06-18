package com.stsporting.core;

/**
 * Receives pointer/key input already translated into virtual (world)
 * coordinates by {@link InputRouter}. Screens that need input implement this.
 * Return true if the event was handled.
 */
public interface InputConsumer {
    boolean onTouchDown(float vx, float vy, int button);

    boolean onTouchUp(float vx, float vy, int button);

    boolean onMouseMoved(float vx, float vy);

    boolean onKeyDown(int keycode);
}

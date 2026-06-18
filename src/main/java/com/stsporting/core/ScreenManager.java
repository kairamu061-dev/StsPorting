package com.stsporting.core;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Manages a stack of {@link GameScreen}s. The top of the stack is the active
 * screen that gets rendered and receives input.
 *
 * <p>Lifecycle contract:
 * <ul>
 *   <li>{@link #replace} swaps the active screen (old one hidden + disposed).</li>
 *   <li>{@link #push} overlays a screen (old one hidden but kept).</li>
 *   <li>{@link #pop} removes the top overlay (hidden + disposed) and re-shows
 *       the one beneath.</li>
 * </ul>
 *
 * <p>This class is deliberately free of any GL/libGDX rendering dependency so
 * the stack behaviour can be unit-tested headlessly.
 */
public class ScreenManager {
    private final Deque<GameScreen> stack = new ArrayDeque<>();

    /** Replace the active screen: hide+dispose the current top, then show next. */
    public void replace(GameScreen next) {
        GameScreen top = stack.peek();
        if (top != null) {
            top.hide();
            top.dispose();
            stack.pop();
        }
        stack.push(next);
        next.show();
    }

    /** Overlay a screen on top; the previous top is hidden but not disposed. */
    public void push(GameScreen overlay) {
        GameScreen top = stack.peek();
        if (top != null) {
            top.hide();
        }
        stack.push(overlay);
        overlay.show();
    }

    /** Remove the top overlay (hide+dispose) and re-show the one beneath, if any. */
    public void pop() {
        GameScreen top = stack.peek();
        if (top == null) {
            return;
        }
        top.hide();
        top.dispose();
        stack.pop();
        GameScreen beneath = stack.peek();
        if (beneath != null) {
            beneath.show();
        }
    }

    /** The active (top) screen, or null if the stack is empty. */
    public GameScreen current() {
        return stack.peek();
    }

    public void render(float delta) {
        GameScreen top = stack.peek();
        if (top != null) {
            top.render(delta);
        }
    }

    public void resize(int width, int height) {
        for (GameScreen s : stack) {
            s.resize(width, height);
        }
    }

    public void dispose() {
        while (!stack.isEmpty()) {
            stack.pop().dispose();
        }
    }

    public int size() {
        return stack.size();
    }
}

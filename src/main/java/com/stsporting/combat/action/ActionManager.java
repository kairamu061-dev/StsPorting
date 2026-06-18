package com.stsporting.combat.action;

import com.stsporting.combat.CombatState;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * The action queue — backbone of combat resolution. Each {@link #update} pulls
 * one action and advances it; when it finishes, the current slot clears and the
 * <em>next</em> update pulls the new front. That one-step gap is deliberate: it
 * guarantees actions queued via {@link #addToTop} during resolution are handled
 * next, reproducing the original's interrupt ordering.
 *
 * <p>GL-independent and deterministic, so resolution order is unit-testable.
 */
public class ActionManager {
    private final CombatState state;
    private GameAction current;
    private final Deque<GameAction> queue = new ArrayDeque<>();

    public ActionManager(CombatState state) {
        this.state = state;
    }

    public CombatState state() {
        return state;
    }

    /** Normal sequential add (resolves after everything already queued). */
    public void addToBottom(GameAction action) {
        action.attach(this);
        queue.addLast(action);
    }

    /** Interrupt: resolves before the rest of the queue. Last added is first out. */
    public void addToTop(GameAction action) {
        action.attach(this);
        queue.addFirst(action);
    }

    public void update(float delta) {
        if (current == null) {
            if (queue.isEmpty()) {
                return;
            }
            current = queue.pollFirst();
        }
        current.update(delta);
        if (current.isDone) {
            current = null;
        }
    }

    /** True when nothing is resolving and the queue is empty (input allowed). */
    public boolean isIdle() {
        return current == null && queue.isEmpty();
    }

    public int queueSize() {
        return queue.size();
    }

    /**
     * Test/headless helper: advance with a fixed delta until idle. The large
     * delta lets duration-based actions complete in a single step. Guarded
     * against runaway loops.
     */
    public void runToCompletion() {
        int guard = 0;
        while (!isIdle()) {
            update(1f);
            if (++guard > 100_000) {
                throw new IllegalStateException("Action queue did not converge");
            }
        }
    }
}

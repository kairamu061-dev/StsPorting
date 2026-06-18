package com.stsporting.combat.action;

import com.stsporting.combat.CombatState;

/**
 * A single unit of combat resolution. The {@link ActionManager} advances the
 * current action each frame via {@link #update(float)} until {@link #isDone}.
 * An action may queue follow-up actions (notably {@code mgr.addToTop} for
 * interrupts) while it runs, reproducing the original's resolution order.
 *
 * <p>{@code duration} models an animation/wait window: time-based actions stay
 * active until it elapses so the next action doesn't resolve mid-animation.
 */
public abstract class GameAction {
    protected float duration;
    protected float startDuration;
    public boolean isDone;

    protected ActionManager mgr;
    protected CombatState state;

    /** Called by ActionManager when the action is enqueued. */
    void attach(ActionManager mgr) {
        this.mgr = mgr;
        this.state = mgr.state();
    }

    /** Advance this action by delta seconds. Set isDone when finished. */
    public abstract void update(float delta);

    /** Convenience for pure wait/animation actions: count down then finish. */
    protected void tickDuration(float delta) {
        duration -= delta;
        if (duration <= 0f) {
            isDone = true;
        }
    }
}

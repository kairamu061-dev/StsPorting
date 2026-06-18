package com.stsporting.combat.action;

/** Pure delay: holds the queue for a fixed duration (e.g. pacing a sequence). */
public class WaitAction extends GameAction {
    public WaitAction(float seconds) {
        this.duration = seconds;
        this.startDuration = seconds;
    }

    @Override
    public void update(float delta) {
        tickDuration(delta);
    }
}

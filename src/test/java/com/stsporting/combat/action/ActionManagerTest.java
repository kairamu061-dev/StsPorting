package com.stsporting.combat.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.stsporting.combat.CombatState;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Headless tests for queue ordering, interrupts, and duration waits. */
class ActionManagerTest {

    /** Logs its name once when first updated, optionally enqueuing more, then finishes. */
    private static final class Rec extends GameAction {
        final String name;
        final List<String> log;
        final Runnable onRun;
        private boolean ran;

        Rec(String name, List<String> log, Runnable onRun) {
            this.name = name;
            this.log = log;
            this.onRun = onRun;
        }

        @Override
        public void update(float delta) {
            if (!ran) {
                ran = true;
                log.add(name);
                if (onRun != null) {
                    onRun.run();
                }
            }
            isDone = true;
        }
    }

    private ActionManager newManager() {
        return new ActionManager(new CombatState());
    }

    @Test
    void resolvesInFifoForAddToBottom() {
        List<String> log = new ArrayList<>();
        ActionManager mgr = newManager();
        mgr.addToBottom(new Rec("A", log, null));
        mgr.addToBottom(new Rec("B", log, null));
        mgr.addToBottom(new Rec("C", log, null));
        mgr.runToCompletion();
        assertEquals(List.of("A", "B", "C"), log);
        assertTrue(mgr.isIdle());
    }

    @Test
    void interruptResolvesBeforeRemainingQueue() {
        List<String> log = new ArrayList<>();
        ActionManager mgr = newManager();
        // A queues B to the top while running; B must resolve before C.
        mgr.addToBottom(new Rec("A", log, () -> mgr.addToTop(new Rec("B", log, null))));
        mgr.addToBottom(new Rec("C", log, null));
        mgr.runToCompletion();
        assertEquals(List.of("A", "B", "C"), log);
    }

    @Test
    void multipleInterruptsAreLastInFirstOut() {
        List<String> log = new ArrayList<>();
        ActionManager mgr = newManager();
        mgr.addToBottom(new Rec("A", log, () -> {
            mgr.addToTop(new Rec("B", log, null));
            mgr.addToTop(new Rec("D", log, null)); // pushed last -> resolves first
        }));
        mgr.runToCompletion();
        assertEquals(List.of("A", "D", "B"), log);
    }

    @Test
    void durationActionHoldsTheQueue() {
        ActionManager mgr = newManager();
        mgr.addToBottom(new WaitAction(0.5f));
        mgr.update(0.2f);
        assertFalse(mgr.isIdle(), "wait should still be running");
        mgr.update(0.4f); // total 0.6 >= 0.5
        assertTrue(mgr.isIdle(), "wait should be finished");
    }
}

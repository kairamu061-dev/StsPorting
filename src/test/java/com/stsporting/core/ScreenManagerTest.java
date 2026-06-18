package com.stsporting.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Headless tests for {@link ScreenManager} stack lifecycle. No GL context is
 * needed because GameScreen is independent of libGDX rendering.
 */
class ScreenManagerTest {

    /** Records lifecycle calls so we can assert ordering. */
    private static final class FakeScreen implements GameScreen {
        final String name;
        final List<String> log;

        FakeScreen(String name, List<String> log) {
            this.name = name;
            this.log = log;
        }

        @Override public void show() { log.add(name + ":show"); }
        @Override public void render(float delta) { log.add(name + ":render"); }
        @Override public void resize(int w, int h) { log.add(name + ":resize"); }
        @Override public void hide() { log.add(name + ":hide"); }
        @Override public void dispose() { log.add(name + ":dispose"); }
    }

    @Test
    void replaceShowsNextAndDisposesPrevious() {
        List<String> log = new ArrayList<>();
        ScreenManager sm = new ScreenManager();
        FakeScreen a = new FakeScreen("A", log);
        FakeScreen b = new FakeScreen("B", log);

        sm.replace(a);
        assertSame(a, sm.current());
        assertEquals(1, sm.size());

        log.clear();
        sm.replace(b);
        assertEquals(List.of("A:hide", "A:dispose", "B:show"), log);
        assertSame(b, sm.current());
        assertEquals(1, sm.size());
    }

    @Test
    void pushOverlaysAndPopRestores() {
        List<String> log = new ArrayList<>();
        ScreenManager sm = new ScreenManager();
        FakeScreen base = new FakeScreen("BASE", log);
        FakeScreen overlay = new FakeScreen("OV", log);

        sm.replace(base);
        log.clear();

        sm.push(overlay);
        assertEquals(List.of("BASE:hide", "OV:show"), log);
        assertSame(overlay, sm.current());
        assertEquals(2, sm.size());

        log.clear();
        sm.pop();
        // overlay is hidden+disposed; base is re-shown (not disposed).
        assertEquals(List.of("OV:hide", "OV:dispose", "BASE:show"), log);
        assertSame(base, sm.current());
        assertEquals(1, sm.size());
    }

    @Test
    void renderForwardsToTopOnly() {
        List<String> log = new ArrayList<>();
        ScreenManager sm = new ScreenManager();
        FakeScreen base = new FakeScreen("BASE", log);
        FakeScreen overlay = new FakeScreen("OV", log);
        sm.replace(base);
        sm.push(overlay);
        log.clear();

        sm.render(0.016f);
        assertEquals(List.of("OV:render"), log);
    }

    @Test
    void emptyManagerHasNoCurrent() {
        ScreenManager sm = new ScreenManager();
        assertNull(sm.current());
        assertEquals(0, sm.size());
        // Should not throw.
        sm.render(0.016f);
        sm.pop();
    }
}

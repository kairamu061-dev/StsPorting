package com.stsporting.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.stsporting.core.GameContext;
import com.stsporting.core.GameScreen;
import com.stsporting.core.InputConsumer;
import com.stsporting.render.ViewportConfig;
import com.stsporting.run.RunController;

/**
 * Main menu: title plus New Run / Settings / Quit buttons. Labels are ASCII for
 * now because the bundled libGDX font has no Japanese glyphs (a JP font is
 * pending per dev-notes).
 */
public class MainMenuScreen implements GameScreen, InputConsumer {
    private static final float BTN_W = 420f;
    private static final float BTN_H = 90f;

    private final GameContext ctx;
    private final ShapeRenderer shapes = new ShapeRenderer();
    private final GlyphLayout layout = new GlyphLayout();

    private final Button[] buttons;

    private static final class Button {
        final String label;
        final Rectangle rect;
        final Runnable action;
        boolean hovered;

        Button(String label, Rectangle rect, Runnable action) {
            this.label = label;
            this.rect = rect;
            this.action = action;
        }
    }

    public MainMenuScreen(GameContext ctx) {
        this.ctx = ctx;
        float cx = (ViewportConfig.VIRTUAL_WIDTH - BTN_W) / 2f;
        this.buttons = new Button[] {
                new Button("New Run", new Rectangle(cx, 560, BTN_W, BTN_H),
                        () -> startRun(ctx)),
                new Button("Settings", new Rectangle(cx, 450, BTN_W, BTN_H),
                        () -> { /* placeholder; settings UI is optional/future */ }),
                new Button("Quit", new Rectangle(cx, 340, BTN_W, BTN_H),
                        () -> Gdx.app.exit()),
        };
    }

    private static void startRun(GameContext ctx) {
        ScreenRunNavigator nav = new ScreenRunNavigator(ctx);
        RunController controller = new RunController(nav, System.nanoTime());
        nav.setController(controller);
        controller.start();
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        shapes.setProjectionMatrix(ctx.camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        for (Button b : buttons) {
            if (b.hovered) {
                shapes.setColor(0.45f, 0.32f, 0.18f, 1f);
            } else {
                shapes.setColor(0.22f, 0.17f, 0.12f, 1f);
            }
            shapes.rect(b.rect.x, b.rect.y, b.rect.width, b.rect.height);
        }
        shapes.end();

        BitmapFont font = ctx.assets.defaultFont();
        ctx.batch.begin();
        font.setColor(0.78f, 0.67f, 0.43f, 1f);
        font.getData().setScale(3.5f);
        layout.setText(font, "SLAY THE SPIRE (port)");
        font.draw(ctx.batch, layout,
                (ViewportConfig.VIRTUAL_WIDTH - layout.width) / 2f, 880);

        font.setColor(Color.WHITE);
        font.getData().setScale(2.2f);
        for (Button b : buttons) {
            layout.setText(font, b.label);
            font.draw(ctx.batch, layout,
                    b.rect.x + (b.rect.width - layout.width) / 2f,
                    b.rect.y + (b.rect.height + layout.height) / 2f);
        }
        ctx.batch.end();
    }

    @Override
    public boolean onMouseMoved(float vx, float vy) {
        for (Button b : buttons) {
            b.hovered = b.rect.contains(vx, vy);
        }
        return false;
    }

    @Override
    public boolean onTouchDown(float vx, float vy, int button) {
        for (Button b : buttons) {
            if (b.rect.contains(vx, vy)) {
                b.action.run();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onTouchUp(float vx, float vy, int button) {
        return false;
    }

    @Override
    public boolean onKeyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            Gdx.app.exit();
            return true;
        }
        return false;
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        shapes.dispose();
    }
}

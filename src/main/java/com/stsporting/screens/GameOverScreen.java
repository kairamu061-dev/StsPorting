package com.stsporting.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.stsporting.core.GameContext;
import com.stsporting.core.GameScreen;
import com.stsporting.core.InputConsumer;
import com.stsporting.run.RunState;

/** End-of-run screen (victory or defeat); any input returns to the main menu. */
public class GameOverScreen implements GameScreen, InputConsumer {
    private final GameContext ctx;
    private final RunState run;
    private final boolean victory;
    private final GlyphLayout layout = new GlyphLayout();

    public GameOverScreen(GameContext ctx, RunState run, boolean victory) {
        this.ctx = ctx;
        this.run = run;
        this.victory = victory;
    }

    @Override
    public void render(float delta) {
        BitmapFont font = ctx.assets.defaultFont();
        ctx.batch.begin();
        font.setColor(victory ? Color.valueOf("c8aa6eff") : Color.valueOf("e03a3aff"));
        font.getData().setScale(4f);
        String title = victory ? "VICTORY" : "GAME OVER";
        layout.setText(font, title);
        font.draw(ctx.batch, layout, (1920 - layout.width) / 2f, 700);

        font.setColor(Color.WHITE);
        font.getData().setScale(1.8f);
        layout.setText(font, "Reached floor " + run.floor + "   -   click to return to menu");
        font.draw(ctx.batch, layout, (1920 - layout.width) / 2f, 540);
        ctx.batch.end();
    }

    private void toMenu() {
        ctx.screens.replace(new MainMenuScreen(ctx));
    }

    @Override
    public boolean onTouchDown(float vx, float vy, int button) {
        toMenu();
        return true;
    }

    @Override
    public boolean onKeyDown(int keycode) {
        toMenu();
        return true;
    }

    @Override public boolean onTouchUp(float vx, float vy, int button) {
        return false;
    }

    @Override public boolean onMouseMoved(float vx, float vy) {
        return false;
    }

    @Override public void show() {
    }

    @Override public void resize(int width, int height) {
    }

    @Override public void hide() {
    }

    @Override public void dispose() {
    }
}

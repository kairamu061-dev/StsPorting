package com.stsporting.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.stsporting.content.cards.CardId;
import com.stsporting.core.GameContext;
import com.stsporting.core.GameScreen;
import com.stsporting.core.InputConsumer;
import com.stsporting.run.RunController;
import java.util.List;

/** Post-combat card reward: pick one of the offered cards or skip. */
public class RewardScreen implements GameScreen, InputConsumer {
    private final GameContext ctx;
    private final RunController controller;
    private final ShapeRenderer shapes = new ShapeRenderer();
    private final GlyphLayout layout = new GlyphLayout();

    private final List<CardId> choices;
    private final Rectangle[] cardRects;
    private final Rectangle skipBtn = new Rectangle(810, 200, 300, 90);

    public RewardScreen(GameContext ctx, RunController controller) {
        this.ctx = ctx;
        this.controller = controller;
        this.choices = controller.rewardCardChoices();
        this.cardRects = new Rectangle[choices.size()];
        float w = 240;
        float h = 340;
        float gap = 60;
        float total = choices.size() * w + (choices.size() - 1) * gap;
        float startX = (1920 - total) / 2f;
        for (int i = 0; i < choices.size(); i++) {
            cardRects[i] = new Rectangle(startX + i * (w + gap), 480, w, h);
        }
    }

    @Override
    public void render(float delta) {
        shapes.setProjectionMatrix(ctx.camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        for (Rectangle r : cardRects) {
            shapes.setColor(0.24f, 0.20f, 0.14f, 1f);
            shapes.rect(r.x, r.y, r.width, r.height);
        }
        shapes.setColor(0.20f, 0.16f, 0.10f, 1f);
        shapes.rect(skipBtn.x, skipBtn.y, skipBtn.width, skipBtn.height);
        shapes.end();

        BitmapFont font = ctx.assets.defaultFont();
        ctx.batch.begin();
        font.setColor(Color.valueOf("c8aa6eff"));
        font.getData().setScale(2.6f);
        layout.setText(font, "Combat Reward - choose a card");
        font.draw(ctx.batch, layout, (1920 - layout.width) / 2f, 900);

        font.setColor(Color.WHITE);
        font.getData().setScale(1.8f);
        for (int i = 0; i < choices.size(); i++) {
            Rectangle r = cardRects[i];
            layout.setText(font, choices.get(i).name());
            font.draw(ctx.batch, layout, r.x + (r.width - layout.width) / 2f, r.y + r.height - 40);
        }
        layout.setText(font, "Skip");
        font.draw(ctx.batch, layout, skipBtn.x + (skipBtn.width - layout.width) / 2f, skipBtn.y + 58);
        ctx.batch.end();
    }

    @Override
    public boolean onTouchDown(float vx, float vy, int button) {
        for (int i = 0; i < cardRects.length; i++) {
            if (cardRects[i].contains(vx, vy)) {
                controller.takeRewardCard(choices.get(i));
                return true;
            }
        }
        if (skipBtn.contains(vx, vy)) {
            controller.skipReward();
            return true;
        }
        return false;
    }

    @Override public boolean onTouchUp(float vx, float vy, int button) {
        return false;
    }

    @Override public boolean onMouseMoved(float vx, float vy) {
        return false;
    }

    @Override public boolean onKeyDown(int keycode) {
        return false;
    }

    @Override public void show() {
    }

    @Override public void resize(int width, int height) {
    }

    @Override public void hide() {
    }

    @Override public void dispose() {
        shapes.dispose();
    }
}

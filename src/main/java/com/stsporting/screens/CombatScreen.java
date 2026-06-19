package com.stsporting.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.stsporting.combat.CombatState;
import com.stsporting.combat.action.ActionManager;
import com.stsporting.combat.card.AbstractCard;
import com.stsporting.combat.creature.Creature;
import com.stsporting.combat.creature.Player;
import com.stsporting.combat.creature.enemy.AbstractMonster;
import com.stsporting.combat.creature.enemy.Intent;
import com.stsporting.combat.flow.TurnController;
import com.stsporting.combat.input.CombatInputController;
import com.stsporting.combat.input.HandLayout;
import com.stsporting.combat.input.InputState;
import com.stsporting.combat.input.Pose;
import com.stsporting.combat.input.TargetResolver;
import com.stsporting.content.cards.CardLibrary;
import com.stsporting.content.monsters.MonsterId;
import com.stsporting.content.monsters.MonsterLibrary;
import com.stsporting.core.GameContext;
import com.stsporting.core.GameScreen;
import com.stsporting.core.InputConsumer;
import com.stsporting.render.ViewportConfig;
import java.util.Random;

/**
 * A playable single combat: starter deck vs one enemy, driven by
 * {@link TurnController}. Hand input (hover-lift, drag-to-play, target arrow)
 * is handled by {@link CombatInputController}; rendering is still minimal
 * (shapes + text) pending the full effects layer.
 */
public class CombatScreen implements GameScreen, InputConsumer {
    private static final float PLAY_THRESHOLD_Y = 430f;

    private final GameContext ctx;
    private final ShapeRenderer shapes = new ShapeRenderer();
    private final GlyphLayout layout = new GlyphLayout();

    private final CombatState state;
    private final ActionManager mgr;
    private final TurnController tc;
    private final HandLayout hand = new HandLayout();
    private final CombatInputController input;

    private final Rectangle endTurnBtn = new Rectangle(1600, 130, 260, 90);
    private final Rectangle enemyBox = new Rectangle(1150, 600, 360, 260);

    public CombatScreen(GameContext ctx) {
        this.ctx = ctx;
        this.state = new CombatState(new Player());
        this.state.rng = new Random(System.nanoTime());
        this.state.drawPile.addAll(CardLibrary.starterDeck());
        AbstractMonster enemy = MonsterLibrary.newMonster(MonsterId.CULTIST);
        enemy.initialize(state.rng);
        this.state.enemies.add(enemy);
        this.mgr = new ActionManager(state);
        this.tc = new TurnController(state, mgr);
        TargetResolver targets = (vx, vy) -> {
            Creature e = firstEnemy();
            return (e != null && !e.isDead() && enemyBox.contains(vx, vy)) ? e : null;
        };
        this.input = new CombatInputController(state, mgr, hand, targets,
                PLAY_THRESHOLD_Y, tc::isPlayerInputAllowed);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        tc.update(delta);
        drawShapes();
        drawText();
    }

    private boolean over() {
        return tc.phase() == TurnController.Phase.FINISHED;
    }

    private Creature firstEnemy() {
        return state.enemies.isEmpty() ? null : state.enemies.get(0);
    }

    /** Render rectangle for a hand card, accounting for hover lift and drag. */
    private Rectangle cardRect(int index, AbstractCard card) {
        int n = state.hand.size();
        float w = hand.cardW;
        float h = hand.cardH;
        if (card == input.draggingCard()) {
            return new Rectangle(input.dragX() - w / 2f, input.dragY() - h / 2f, w, h);
        }
        Pose p = hand.poseFor(index, n);
        boolean hov = card == input.hoveredCard() && input.state() == InputState.HOVER;
        float scale = hov ? 1.18f : 1f;
        float lift = hov ? 60f : 0f;
        float ww = w * scale;
        float hh = h * scale;
        return new Rectangle(p.x - ww / 2f, p.y - hh / 2f + lift, ww, hh);
    }

    private void drawShapes() {
        shapes.setProjectionMatrix(ctx.camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);

        Creature enemy = firstEnemy();
        if (enemy != null && !enemy.isDead()) {
            shapes.setColor(0.30f, 0.12f, 0.12f, 1f);
            shapes.rect(enemyBox.x, enemyBox.y, enemyBox.width, enemyBox.height);
        }

        shapes.setColor(0.12f, 0.18f, 0.26f, 1f);
        shapes.rect(220, 380, 360, 220);

        int n = state.hand.size();
        for (int i = 0; i < n; i++) {
            AbstractCard c = state.hand.get(i);
            Rectangle r = cardRect(i, c);
            boolean playable = state.energy >= c.cost();
            if (c == input.draggingCard()) {
                shapes.setColor(0.55f, 0.42f, 0.20f, 1f);
            } else if (playable) {
                shapes.setColor(0.24f, 0.20f, 0.14f, 1f);
            } else {
                shapes.setColor(0.15f, 0.12f, 0.10f, 1f);
            }
            shapes.rect(r.x, r.y, r.width, r.height);
        }

        shapes.setColor(0.20f, 0.16f, 0.10f, 1f);
        shapes.rect(endTurnBtn.x, endTurnBtn.y, endTurnBtn.width, endTurnBtn.height);
        shapes.end();

        // Targeting arrow.
        if (input.state() == InputState.TARGETING && input.draggingCard() != null) {
            int idx = state.hand.indexOf(input.draggingCard());
            if (idx >= 0) {
                Pose origin = hand.poseFor(idx, n);
                shapes.begin(ShapeRenderer.ShapeType.Line);
                shapes.setColor(0.9f, 0.75f, 0.3f, 1f);
                shapes.line(origin.x, origin.y, input.dragX(), input.dragY());
                shapes.end();
            }
        }
    }

    private void drawText() {
        BitmapFont font = ctx.assets.defaultFont();
        ctx.batch.begin();
        font.setColor(Color.WHITE);

        Creature enemy = firstEnemy();
        font.getData().setScale(1.8f);
        if (enemy != null && !enemy.isDead()) {
            text(font, enemy.name + "  HP " + enemy.currentHp + "/" + enemy.maxHp
                    + (enemy.block > 0 ? "  Blk " + enemy.block : ""), enemyBox.x + 16, enemyBox.y + 230);
            if (enemy instanceof AbstractMonster) {
                text(font, intentText(((AbstractMonster) enemy).getIntent(state.player)),
                        enemyBox.x + 16, enemyBox.y + 180);
            }
        }

        Player p = state.player;
        text(font, "Ironclad  HP " + p.currentHp + "/" + p.maxHp
                + (p.block > 0 ? "  Blk " + p.block : ""), 240, 560);
        text(font, "Energy " + state.energy + "/" + state.maxEnergy, 240, 250);
        text(font, "Draw " + state.drawPile.size() + "   Discard " + state.discardPile.size(), 240, 200);

        font.getData().setScale(1.3f);
        int n = state.hand.size();
        for (int i = 0; i < n; i++) {
            AbstractCard c = state.hand.get(i);
            Rectangle r = cardRect(i, c);
            text(font, "(" + c.cost() + ") " + c.name, r.x + 12, r.y + r.height - 22);
        }

        font.getData().setScale(1.8f);
        text(font, "End Turn", endTurnBtn.x + 30, endTurnBtn.y + 58);

        if (over()) {
            font.getData().setScale(3f);
            String msg = state.isPlayerDead() ? "DEFEAT" : "VICTORY";
            text(font, msg + " - press ESC for menu", 700, 980);
        }
        ctx.batch.end();
    }

    private void text(BitmapFont font, String s, float x, float y) {
        layout.setText(font, s);
        font.draw(ctx.batch, layout, x, y);
    }

    private String intentText(Intent intent) {
        switch (intent.type) {
            case ATTACK:
            case ATTACK_MULTI:
            case ATTACK_DEFEND:
                return "Intent: Attack " + intent.damage + (intent.hits > 1 ? " x" + intent.hits : "");
            case DEFEND:
                return "Intent: Defend";
            case BUFF:
                return "Intent: Buff";
            case DEBUFF:
                return "Intent: Debuff";
            default:
                return "Intent: ?";
        }
    }

    @Override
    public boolean onTouchDown(float vx, float vy, int button) {
        if (over()) {
            return false;
        }
        if (tc.isPlayerInputAllowed() && endTurnBtn.contains(vx, vy)) {
            tc.requestEndTurn();
            return true;
        }
        return input.onTouchDown(vx, vy);
    }

    @Override
    public boolean onTouchUp(float vx, float vy, int button) {
        return input.onTouchUp(vx, vy);
    }

    @Override
    public boolean onMouseMoved(float vx, float vy) {
        input.onMouseMoved(vx, vy);
        return false;
    }

    @Override
    public boolean onKeyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            ctx.screens.replace(new MainMenuScreen(ctx));
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

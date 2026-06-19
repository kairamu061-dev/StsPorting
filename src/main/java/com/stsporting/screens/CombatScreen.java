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
import com.stsporting.combat.card.CardTarget;
import com.stsporting.combat.card.PlayCardFlow;
import com.stsporting.combat.creature.Creature;
import com.stsporting.combat.creature.Player;
import com.stsporting.combat.creature.enemy.AbstractMonster;
import com.stsporting.combat.creature.enemy.Intent;
import com.stsporting.combat.flow.TurnController;
import com.stsporting.content.cards.CardLibrary;
import com.stsporting.content.monsters.MonsterId;
import com.stsporting.content.monsters.MonsterLibrary;
import com.stsporting.core.GameContext;
import com.stsporting.core.GameScreen;
import com.stsporting.core.InputConsumer;
import com.stsporting.render.ViewportConfig;
import java.util.Random;

/**
 * A playable single combat: sets up the player's starter deck against one
 * enemy and drives {@link TurnController}. Click a card to play it (targeted
 * attacks then click the enemy); click End Turn to pass. Minimal rendering —
 * the full effects layer is future work; this proves the vertical slice.
 */
public class CombatScreen implements GameScreen, InputConsumer {
    private final GameContext ctx;
    private final ShapeRenderer shapes = new ShapeRenderer();
    private final GlyphLayout layout = new GlyphLayout();

    private final CombatState state;
    private final ActionManager mgr;
    private final TurnController tc;

    private final Rectangle endTurnBtn = new Rectangle(1600, 130, 260, 90);
    private final Rectangle enemyBox = new Rectangle(1150, 600, 360, 260);
    private final Rectangle[] handRects = new Rectangle[CombatState.MAX_HAND];

    private AbstractCard selectedCard; // pending targeted card

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

    private void drawShapes() {
        shapes.setProjectionMatrix(ctx.camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);

        // Enemy box
        Creature enemy = firstEnemy();
        if (enemy != null && !enemy.isDead()) {
            shapes.setColor(0.30f, 0.12f, 0.12f, 1f);
            shapes.rect(enemyBox.x, enemyBox.y, enemyBox.width, enemyBox.height);
        }

        // Player box
        shapes.setColor(0.12f, 0.18f, 0.26f, 1f);
        shapes.rect(220, 380, 360, 220);

        // Hand cards
        int n = state.hand.size();
        for (int i = 0; i < n; i++) {
            Rectangle r = handRect(i, n);
            handRects[i] = r;
            AbstractCard c = state.hand.get(i);
            boolean playable = state.energy >= c.cost();
            if (c == selectedCard) {
                shapes.setColor(0.55f, 0.42f, 0.20f, 1f);
            } else if (playable) {
                shapes.setColor(0.24f, 0.20f, 0.14f, 1f);
            } else {
                shapes.setColor(0.15f, 0.12f, 0.10f, 1f);
            }
            shapes.rect(r.x, r.y, r.width, r.height);
        }

        // End turn button
        shapes.setColor(0.20f, 0.16f, 0.10f, 1f);
        shapes.rect(endTurnBtn.x, endTurnBtn.y, endTurnBtn.width, endTurnBtn.height);

        shapes.end();
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

        // Hand labels
        font.getData().setScale(1.4f);
        int n = state.hand.size();
        for (int i = 0; i < n; i++) {
            Rectangle r = handRects[i];
            if (r == null) {
                continue;
            }
            AbstractCard c = state.hand.get(i);
            text(font, "(" + c.cost() + ") " + c.name, r.x + 12, r.y + r.height - 24);
        }

        font.getData().setScale(1.8f);
        text(font, "End Turn", endTurnBtn.x + 30, endTurnBtn.y + 58);

        if (!tc.isPlayerInputAllowed() && !over()) {
            text(font, "...", 940, 540);
        }

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

    private Rectangle handRect(int index, int count) {
        float w = 200f;
        float h = 280f;
        float gap = 20f;
        float totalW = count * w + (count - 1) * gap;
        float startX = (ViewportConfig.VIRTUAL_WIDTH - totalW) / 2f;
        return new Rectangle(startX + index * (w + gap), 40, w, h);
    }

    @Override
    public boolean onTouchDown(float vx, float vy, int button) {
        if (over()) {
            return false;
        }
        if (!tc.isPlayerInputAllowed()) {
            return false;
        }
        // End turn
        if (endTurnBtn.contains(vx, vy)) {
            selectedCard = null;
            tc.requestEndTurn();
            return true;
        }
        // Enemy click resolves a pending targeted card
        Creature enemy = firstEnemy();
        if (selectedCard != null && enemy != null && enemyBox.contains(vx, vy)) {
            PlayCardFlow.resolve(mgr, selectedCard, enemy);
            selectedCard = null;
            return true;
        }
        // Card click
        int n = state.hand.size();
        for (int i = 0; i < n; i++) {
            Rectangle r = handRects[i];
            if (r != null && r.contains(vx, vy)) {
                AbstractCard c = state.hand.get(i);
                if (state.energy < c.cost()) {
                    return true; // not enough energy
                }
                if (c.target == CardTarget.ENEMY) {
                    selectedCard = c; // await enemy click
                } else {
                    PlayCardFlow.resolve(mgr, c, null);
                    selectedCard = null;
                }
                return true;
            }
        }
        selectedCard = null;
        return true;
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
    public boolean onTouchUp(float vx, float vy, int button) {
        return false;
    }

    @Override
    public boolean onMouseMoved(float vx, float vy) {
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

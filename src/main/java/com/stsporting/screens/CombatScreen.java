package com.stsporting.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.stsporting.combat.CombatListener;
import com.stsporting.combat.CombatState;
import com.stsporting.combat.DamageType;
import com.stsporting.combat.action.ActionManager;
import com.stsporting.combat.card.AbstractCard;
import com.stsporting.combat.creature.Creature;
import com.stsporting.combat.creature.Player;
import com.stsporting.combat.creature.enemy.AbstractMonster;
import com.stsporting.combat.creature.enemy.Intent;
import com.stsporting.combat.creature.enemy.IntentType;
import com.stsporting.combat.power.AbstractPower;
import com.stsporting.combat.flow.TurnController;
import com.stsporting.combat.input.CardAnimator;
import com.stsporting.combat.input.CombatInputController;
import com.stsporting.combat.input.HandLayout;
import com.stsporting.combat.input.InputState;
import com.stsporting.combat.input.TargetResolver;
import com.stsporting.combat.vfx.DamageNumberEffect;
import com.stsporting.combat.vfx.EffectManager;
import com.stsporting.combat.vfx.ScreenShake;
import com.stsporting.content.cards.CardLibrary;
import com.stsporting.content.monsters.MonsterId;
import com.stsporting.content.monsters.MonsterLibrary;
import com.stsporting.core.GameContext;
import com.stsporting.core.GameScreen;
import com.stsporting.core.InputConsumer;
import com.stsporting.render.ViewportConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
    private final CardAnimator cardAnim = new CardAnimator(300f, 150f); // ease in from draw pile
    private final CombatInputController input;
    private final EffectManager fx = new EffectManager();
    private final ScreenShake shake = new ScreenShake();
    private static final float FLASH_TIME = 0.2f;
    private final Map<Creature, Float> flashTimers = new HashMap<>();

    // While aiming a target card it lifts just above the hand (lower, like the
    // original); on play it rises higher (PLAY_PEAK_Y) before arcing to discard.
    private static final float AIM_X = 960f;
    private static final float AIM_Y = 380f;
    private static final float PLAY_PEAK_Y = 880f;
    private float previewX = AIM_X;
    private float previewY = AIM_Y;
    private AbstractCard lastDragging;

    // Cards that have left the hand, flying toward the discard pile.
    private static final float FLY_TIME = 0.45f;
    private static final float DISCARD_X = 1780f;
    private static final float DISCARD_Y = 150f;
    private final List<FlyingCard> flying = new ArrayList<>();
    private final java.util.Set<AbstractCard> playHandled = new java.util.HashSet<>();
    private List<AbstractCard> prevHand = new ArrayList<>();

    private TurnController.Phase lastPhase;
    private String bannerText = "";
    private float bannerTimer;

    /** A card flying out along a quadratic bezier (start -> control -> discard). */
    private static final class FlyingCard {
        final float x0;
        final float y0;
        final float cx;
        final float cy;
        final float x1;
        final float y1;
        final String label;
        float t;

        FlyingCard(float x0, float y0, float cx, float cy, float x1, float y1, String label) {
            this.x0 = x0;
            this.y0 = y0;
            this.cx = cx;
            this.cy = cy;
            this.x1 = x1;
            this.y1 = y1;
            this.label = label;
        }

        float p() {
            return Math.min(1f, t / FLY_TIME);
        }

        float x() {
            float p = p();
            float u = 1f - p;
            return u * u * x0 + 2f * u * p * cx + p * p * x1;
        }

        float y() {
            float p = p();
            float u = 1f - p;
            return u * u * y0 + 2f * u * p * cy + p * p * y1;
        }
    }

    private final Rectangle endTurnBtn = new Rectangle(1600, 130, 260, 90);
    private final Rectangle enemyBox = new Rectangle(1150, 600, 360, 260);
    private final Rectangle playerBox = new Rectangle(220, 380, 360, 220);

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
        this.state.listener = vfxListener();
    }

    private CombatListener vfxListener() {
        return new CombatListener() {
            @Override
            public void onDamageDealt(Creature target, int hpDamage, DamageType type) {
                flashTimers.put(target, FLASH_TIME); // flash even on blocked hits
                if (hpDamage <= 0) {
                    return;
                }
                Vector2 p = posFor(target);
                fx.add(new DamageNumberEffect(p.x, p.y + 40f, hpDamage, Color.SCARLET, 3.4f));
                shake.shake(Math.min(20f, 5f + hpDamage * 0.8f), 0.22f);
            }

            @Override
            public void onBlockGained(Creature target, int amount) {
                Vector2 p = posFor(target);
                fx.add(new DamageNumberEffect(p.x, p.y + 40f, amount, Color.valueOf("6db3ffff"), 2.6f));
            }

            @Override
            public void onHpLost(Creature target, int amount) {
                Vector2 p = posFor(target);
                fx.add(new DamageNumberEffect(p.x, p.y + 40f, amount, Color.valueOf("b066ffff"), 3.0f));
            }
        };
    }

    private void updateTurnBanner(float delta) {
        TurnController.Phase ph = tc.phase();
        if (ph != lastPhase) {
            if (ph == TurnController.Phase.PLAYER) {
                bannerText = "Your Turn";
                bannerTimer = 1.1f;
            } else if (ph == TurnController.Phase.ENEMY) {
                bannerText = "Enemy Turn";
                bannerTimer = 1.1f;
            }
            lastPhase = ph;
        }
        if (bannerTimer > 0f) {
            bannerTimer -= delta;
        }
    }

    private void spawnFlyingForPlayedCards() {
        for (AbstractCard c : prevHand) {
            if (!state.hand.contains(c) && !playHandled.contains(c)) {
                // Discarded (e.g. end of turn): straight glide to the pile.
                float sx = cardAnim.x(c);
                float sy = cardAnim.y(c);
                flying.add(new FlyingCard(sx, sy, (sx + DISCARD_X) / 2f, (sy + DISCARD_Y) / 2f,
                        DISCARD_X, DISCARD_Y, label(c)));
            }
        }
        playHandled.clear();
    }

    /** Played card: rise up (to ~PLAY_PEAK_Y) then arc down to the pile. */
    private void spawnPlayedCardFlyout(AbstractCard c, float fromX, float fromY) {
        flying.add(new FlyingCard(fromX, fromY, fromX, PLAY_PEAK_Y, DISCARD_X, DISCARD_Y, label(c)));
        playHandled.add(c);
    }

    private String label(AbstractCard c) {
        return "(" + c.cost() + ") " + c.name;
    }

    private void updatePreview(float delta) {
        AbstractCard dc = input.draggingCard();
        // Only single-target cards rise to the centre; others follow the cursor.
        if (dc != null && input.state() == InputState.TARGETING) {
            if (dc != lastDragging) { // just picked up: start from its slot
                previewX = cardAnim.x(dc);
                previewY = cardAnim.y(dc);
                lastDragging = dc;
            }
            float t = Math.min(1f, 16f * delta);
            previewX += (AIM_X - previewX) * t;
            previewY += (AIM_Y - previewY) * t;
        } else {
            lastDragging = null;
        }
    }

    private void updateFlashes(float delta) {
        Iterator<Map.Entry<Creature, Float>> it = flashTimers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Creature, Float> e = it.next();
            float t = e.getValue() - delta;
            if (t <= 0f) {
                it.remove();
            } else {
                e.setValue(t);
            }
        }
    }

    /** 0..1 flash strength for a creature (1 = just hit). */
    private float flash(Creature c) {
        Float t = flashTimers.get(c);
        return t == null ? 0f : Math.max(0f, t / FLASH_TIME);
    }

    private Vector2 posFor(Creature c) {
        if (c == state.player) {
            return new Vector2(playerBox.x + playerBox.width / 2f, playerBox.y + playerBox.height / 2f);
        }
        return new Vector2(enemyBox.x + enemyBox.width / 2f, enemyBox.y + enemyBox.height / 2f);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        tc.update(delta);
        fx.update(delta);
        shake.update(delta);
        updateFlashes(delta);
        spawnFlyingForPlayedCards();
        cardAnim.update(state.hand, hand, delta);
        for (int i = flying.size() - 1; i >= 0; i--) {
            FlyingCard f = flying.get(i);
            f.t += delta;
            if (f.t >= FLY_TIME) {
                flying.remove(i);
            }
        }
        prevHand = new ArrayList<>(state.hand);
        updateTurnBanner(delta);
        updatePreview(delta);

        // Apply screen shake by offsetting the (centred) camera, then restore.
        float ox = shake.offsetX();
        float oy = shake.offsetY();
        ctx.camera.position.set(ViewportConfig.VIRTUAL_WIDTH / 2f + ox,
                ViewportConfig.VIRTUAL_HEIGHT / 2f + oy, 0);
        ctx.camera.update();
        ctx.batch.setProjectionMatrix(ctx.camera.combined);

        drawShapes();
        drawText();

        ctx.camera.position.set(ViewportConfig.VIRTUAL_WIDTH / 2f, ViewportConfig.VIRTUAL_HEIGHT / 2f, 0);
        ctx.camera.update();
    }

    private boolean over() {
        return tc.phase() == TurnController.Phase.FINISHED;
    }

    private Creature firstEnemy() {
        return state.enemies.isEmpty() ? null : state.enemies.get(0);
    }

    /** Render rectangle for a hand card, accounting for hover lift and drag. */
    private Rectangle cardRect(AbstractCard card) {
        float w = hand.cardW;
        float h = hand.cardH;
        if (card == input.draggingCard()) {
            if (input.state() == InputState.TARGETING) {
                // Single-target card lifts just above the hand while aiming.
                float pw = w * 1.2f;
                float ph = h * 1.2f;
                return new Rectangle(previewX - pw / 2f, previewY - ph / 2f, pw, ph);
            }
            // Non-target card follows the cursor.
            return new Rectangle(input.dragX() - w / 2f, input.dragY() - h / 2f, w, h);
        }
        float cx = cardAnim.x(card);
        float cy = cardAnim.y(card);
        boolean hov = card == input.hoveredCard() && input.state() == InputState.HOVER;
        float scale = hov ? 1.18f : 1f;
        float lift = hov ? 60f : 0f;
        float ww = w * scale;
        float hh = h * scale;
        return new Rectangle(cx - ww / 2f, cy - hh / 2f + lift, ww, hh);
    }

    private void drawShapes() {
        shapes.setProjectionMatrix(ctx.camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Filled);

        Creature enemy = firstEnemy();
        if (enemy != null && !enemy.isDead()) {
            setFlashed(0.30f, 0.12f, 0.12f, flash(enemy));
            shapes.rect(enemyBox.x, enemyBox.y, enemyBox.width, enemyBox.height);
            drawPowerChipShapes(enemy, enemyBox);
            if (enemy instanceof AbstractMonster) {
                Color ic = intentColor(((AbstractMonster) enemy).getIntent(state.player).type);
                shapes.setColor(ic.r, ic.g, ic.b, 1f);
                shapes.rect(enemyBox.x + enemyBox.width - 70, enemyBox.y + enemyBox.height - 70, 54, 54);
            }
        }

        setFlashed(0.12f, 0.18f, 0.26f, state.player == null ? 0f : flash(state.player));
        shapes.rect(playerBox.x, playerBox.y, playerBox.width, playerBox.height);
        if (state.player != null) {
            drawPowerChipShapes(state.player, playerBox);
        }

        int n = state.hand.size();
        for (int i = 0; i < n; i++) {
            AbstractCard c = state.hand.get(i);
            Rectangle r = cardRect(c);
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

        // Flying (played/discarded) cards shrinking along their bezier path.
        for (FlyingCard f : flying) {
            float p = f.p();
            float s = 1f - 0.7f * p;
            float w = hand.cardW * s;
            float h = hand.cardH * s;
            float x = f.x();
            float y = f.y();
            shapes.setColor(0.24f, 0.20f, 0.14f, 1f);
            shapes.rect(x - w / 2f, y - h / 2f, w, h);
        }

        shapes.setColor(0.20f, 0.16f, 0.10f, 1f);
        shapes.rect(endTurnBtn.x, endTurnBtn.y, endTurnBtn.width, endTurnBtn.height);
        shapes.end();

        // Targeting arrow: from the centred preview card to the cursor.
        if (input.state() == InputState.TARGETING && input.draggingCard() != null) {
            shapes.begin(ShapeRenderer.ShapeType.Line);
            shapes.setColor(0.9f, 0.3f, 0.25f, 1f);
            shapes.line(previewX, previewY + hand.cardH * 0.6f, input.dragX(), input.dragY());
            shapes.end();
        }
    }

    private static final float CHIP = 46f;
    private static final float CHIP_GAP = 8f;

    private float powersAnchorX(Rectangle box) {
        return box.x + 16f;
    }

    private float powersAnchorY(Rectangle box) {
        return box.y - 66f;
    }

    private Color powerColor(String id) {
        switch (id) {
            case "Strength": return Color.valueOf("e08a3cff");
            case "Weak": return Color.valueOf("7faa5aff");
            case "Vulnerable": return Color.valueOf("b066ffff");
            case "Ritual": return Color.valueOf("d4b24aff");
            case "Thorns": return Color.valueOf("9aa0a6ff");
            case "Poison": return Color.valueOf("6db36dff");
            case "Metallicize": return Color.valueOf("9fb6c9ff");
            case "Regen": return Color.valueOf("7fe0a0ff");
            default: return Color.LIGHT_GRAY;
        }
    }

    private String powerShort(String id) {
        switch (id) {
            case "Strength": return "Str";
            case "Weak": return "Wk";
            case "Vulnerable": return "Vul";
            case "Ritual": return "Rit";
            case "Thorns": return "Tho";
            case "Poison": return "Poi";
            case "Metallicize": return "Met";
            case "Regen": return "Rgn";
            default: return id.substring(0, Math.min(3, id.length()));
        }
    }

    private Color intentColor(IntentType type) {
        if (type.isAttack()) {
            return Color.valueOf("e0503aff");
        }
        switch (type) {
            case DEFEND: return Color.valueOf("4aa3e0ff");
            case BUFF: return Color.valueOf("d4b24aff");
            case DEBUFF: return Color.valueOf("b066ffff");
            default: return Color.LIGHT_GRAY;
        }
    }

    private void drawPowerChipShapes(Creature c, Rectangle box) {
        float x0 = powersAnchorX(box);
        float y = powersAnchorY(box);
        for (int i = 0; i < c.powers.size(); i++) {
            AbstractPower p = c.powers.get(i);
            Color col = powerColor(p.id);
            shapes.setColor(col.r, col.g, col.b, 1f);
            shapes.rect(x0 + i * (CHIP + CHIP_GAP), y, CHIP, CHIP);
        }
    }

    private void drawPowerChipLabels(Creature c, Rectangle box, BitmapFont font) {
        float x0 = powersAnchorX(box);
        float y = powersAnchorY(box);
        font.getData().setScale(1.0f);
        for (int i = 0; i < c.powers.size(); i++) {
            AbstractPower p = c.powers.get(i);
            float x = x0 + i * (CHIP + CHIP_GAP);
            text(font, powerShort(p.id), x + 2, y + CHIP + 24);
            text(font, Integer.toString(p.amount), x + 14, y + 30);
        }
    }

    /** Set the shape colour, blended toward white by the flash strength. */
    private void setFlashed(float r, float g, float b, float flash) {
        float k = flash * 0.7f;
        shapes.setColor(r + (1f - r) * k, g + (1f - g) * k, b + (1f - b) * k, 1f);
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

        // Power chips + intent value.
        if (enemy != null && !enemy.isDead()) {
            drawPowerChipLabels(enemy, enemyBox, font);
            if (enemy instanceof AbstractMonster) {
                Intent it = ((AbstractMonster) enemy).getIntent(state.player);
                if (it.type.isAttack()) {
                    font.getData().setScale(1.6f);
                    text(font, Integer.toString(it.damage) + (it.hits > 1 ? "x" + it.hits : ""),
                            enemyBox.x + enemyBox.width - 62, enemyBox.y + enemyBox.height - 30);
                }
            }
        }
        drawPowerChipLabels(state.player, playerBox, font);

        font.getData().setScale(1.3f);
        for (AbstractCard c : state.hand) {
            Rectangle r = cardRect(c);
            text(font, "(" + c.cost() + ") " + c.name, r.x + 12, r.y + r.height - 22);
        }
        for (FlyingCard f : flying) {
            font.setColor(1f, 1f, 1f, 1f - f.p());
            text(font, f.label, f.x() - 80, f.y() + 40);
            font.setColor(Color.WHITE);
        }

        font.getData().setScale(1.8f);
        text(font, "End Turn", endTurnBtn.x + 30, endTurnBtn.y + 58);

        if (bannerTimer > 0f && !over()) {
            font.getData().setScale(3.4f);
            font.setColor(1f, 1f, 1f, Math.min(1f, bannerTimer));
            layout.setText(font, bannerText);
            font.draw(ctx.batch, layout, (ViewportConfig.VIRTUAL_WIDTH - layout.width) / 2f, 720);
            font.setColor(Color.WHITE);
        }

        if (over()) {
            font.getData().setScale(3f);
            String msg = state.isPlayerDead() ? "DEFEAT" : "VICTORY";
            text(font, msg + " - press ESC for menu", 700, 980);
        }

        fx.render(ctx.batch, font); // floating numbers on top
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
        AbstractCard dragged = input.draggingCard();
        boolean wasTargeting = input.state() == InputState.TARGETING;
        float px = wasTargeting ? previewX : vx;
        float py = wasTargeting ? previewY : vy;
        boolean played = input.onTouchUp(vx, vy);
        if (played && dragged != null) {
            // Rise from where it was, then arc down to the discard pile.
            spawnPlayedCardFlyout(dragged, px, py);
        }
        return played;
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

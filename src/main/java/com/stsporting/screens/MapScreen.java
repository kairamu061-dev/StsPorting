package com.stsporting.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.stsporting.core.GameContext;
import com.stsporting.core.GameScreen;
import com.stsporting.core.InputConsumer;
import com.stsporting.map.MapGenerator;
import com.stsporting.map.MapGraph;
import com.stsporting.map.MapNode;
import com.stsporting.map.NodeType;
import com.stsporting.run.RunController;
import java.util.Set;

/**
 * Act map: draws the node graph and edges, highlights the nodes reachable from
 * the current position, and forwards a click on one to the controller.
 */
public class MapScreen implements GameScreen, InputConsumer {
    private static final float NODE_R = 24f;
    private static final float X0 = 360f;
    private static final float X_GAP = 200f;
    private static final float Y0 = 170f;
    private static final float Y_GAP = (980f - Y0) / (MapGenerator.ROWS - 1);

    private final GameContext ctx;
    private final RunController controller;
    private final ShapeRenderer shapes = new ShapeRenderer();
    private final GlyphLayout layout = new GlyphLayout();

    public MapScreen(GameContext ctx, RunController controller) {
        this.ctx = ctx;
        this.controller = controller;
    }

    private float xFor(MapNode n) {
        return X0 + n.col * X_GAP;
    }

    private float yFor(MapNode n) {
        return Y0 + n.row * Y_GAP;
    }

    @Override
    public void render(float delta) {
        MapGraph map = controller.map();
        Set<MapNode> selectable = controller.selectable();

        shapes.setProjectionMatrix(ctx.camera.combined);
        shapes.begin(ShapeRenderer.ShapeType.Line);
        shapes.setColor(0.45f, 0.40f, 0.32f, 1f);
        for (var row : map.rows) {
            for (MapNode n : row) {
                for (MapNode nx : n.next) {
                    shapes.line(xFor(n), yFor(n), xFor(nx), yFor(nx));
                }
            }
        }
        shapes.end();

        shapes.begin(ShapeRenderer.ShapeType.Filled);
        for (var row : map.rows) {
            for (MapNode n : row) {
                boolean sel = selectable.contains(n);
                Color c = nodeColor(n.type);
                float k = sel ? 1f : 0.4f;
                shapes.setColor(c.r * k + (sel ? 0.1f : 0f), c.g * k, c.b * k, 1f);
                shapes.circle(xFor(n), yFor(n), sel ? NODE_R + 4f : NODE_R, 16);
            }
        }
        if (controller.current() != null) {
            MapNode cur = controller.current();
            shapes.setColor(1f, 1f, 1f, 1f);
            shapes.circle(xFor(cur), yFor(cur), 8f, 12);
        }
        shapes.end();

        BitmapFont font = ctx.assets.defaultFont();
        ctx.batch.begin();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.4f);
        for (var row : map.rows) {
            for (MapNode n : row) {
                layout.setText(font, glyph(n.type));
                font.draw(ctx.batch, layout, xFor(n) - layout.width / 2f, yFor(n) + layout.height / 2f);
            }
        }
        drawTopBar(font);
        font.getData().setScale(1.5f);
        layout.setText(font, "Select a node");
        font.draw(ctx.batch, layout, (1920 - layout.width) / 2f, 1050);
        ctx.batch.end();
    }

    private void drawTopBar(BitmapFont font) {
        font.getData().setScale(1.6f);
        font.setColor(Color.WHITE);
        var run = controller.run();
        layout.setText(font, "HP " + run.currentHp + "/" + run.maxHp + "    Gold " + run.gold
                + "    Floor " + run.floor + "    Act " + run.act);
        font.draw(ctx.batch, layout, 60, 60);
    }

    private Color nodeColor(NodeType type) {
        switch (type) {
            case MONSTER: return Color.valueOf("c0563aff");
            case ELITE: return Color.valueOf("d23b3bff");
            case REST: return Color.valueOf("4aa3e0ff");
            case MERCHANT: return Color.valueOf("d4b24aff");
            case TREASURE: return Color.valueOf("e0c84aff");
            case EVENT: return Color.valueOf("8e6fd0ff");
            case BOSS: return Color.valueOf("ff3030ff");
            default: return Color.GRAY;
        }
    }

    private String glyph(NodeType type) {
        switch (type) {
            case MONSTER: return "M";
            case ELITE: return "E";
            case REST: return "R";
            case MERCHANT: return "$";
            case TREASURE: return "T";
            case EVENT: return "?";
            case BOSS: return "B";
            default: return "-";
        }
    }

    @Override
    public boolean onTouchDown(float vx, float vy, int button) {
        for (MapNode n : controller.selectable()) {
            float dx = vx - xFor(n);
            float dy = vy - yFor(n);
            if (dx * dx + dy * dy <= (NODE_R + 12f) * (NODE_R + 12f)) {
                controller.onNodeSelected(n);
                return true;
            }
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

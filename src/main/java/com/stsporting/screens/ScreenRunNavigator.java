package com.stsporting.screens;

import com.stsporting.core.GameContext;
import com.stsporting.map.MapNode;
import com.stsporting.run.CombatRequest;
import com.stsporting.run.CombatResult;
import com.stsporting.run.RunController;
import com.stsporting.run.RunNavigator;
import java.util.function.Consumer;

/** Wires {@link RunController} transitions to concrete screens. */
public class ScreenRunNavigator implements RunNavigator {
    private final GameContext ctx;
    private RunController controller;

    public ScreenRunNavigator(GameContext ctx) {
        this.ctx = ctx;
    }

    /** Set after the controller is built (controller and navigator are mutual). */
    public void setController(RunController controller) {
        this.controller = controller;
    }

    @Override
    public void showMap() {
        ctx.screens.replace(new MapScreen(ctx, controller));
    }

    @Override
    public void showCombat(CombatRequest request, Consumer<CombatResult> onResult) {
        ctx.screens.replace(new CombatScreen(ctx, request, onResult));
    }

    @Override
    public void showReward(MapNode node) {
        ctx.screens.replace(new RewardScreen(ctx, controller));
    }

    @Override
    public void showGameOver(boolean victory) {
        ctx.screens.replace(new GameOverScreen(ctx, controller.run(), victory));
    }
}

package com.stsporting.run;

import com.stsporting.map.MapNode;
import java.util.function.Consumer;

/**
 * Screen transitions the run needs, abstracted so {@link RunController} logic
 * can be tested without a GL context (a fake navigator records calls).
 */
public interface RunNavigator {
    void showMap();

    void showCombat(CombatRequest request, Consumer<CombatResult> onResult);

    void showReward(MapNode node);

    void showGameOver(boolean victory);
}

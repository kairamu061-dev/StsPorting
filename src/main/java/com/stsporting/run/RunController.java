package com.stsporting.run;

import com.stsporting.content.cards.CardId;
import com.stsporting.content.monsters.MonsterId;
import com.stsporting.map.MapGenerator;
import com.stsporting.map.MapGraph;
import com.stsporting.map.MapNode;
import com.stsporting.map.NodeType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Orchestrates a run: holds {@link RunState}, the generated map and the RNG, and
 * routes node selections to combats / rewards / non-combat nodes via a
 * {@link RunNavigator}. Pure logic (screen creation lives behind the navigator)
 * so the flow is unit-testable.
 */
public class RunController {
    private final RunNavigator nav;
    private final RunState run;
    private final RunRng rng;
    private final MapGraph map;
    private MapNode current;

    private List<CardId> pendingCardChoices = new ArrayList<>();

    public RunController(RunNavigator nav, long seed) {
        this.nav = nav;
        this.run = RunState.newRun(seed);
        this.rng = new RunRng(seed);
        this.map = new MapGenerator().generate(rng.stream(RngStream.MAP));
    }

    public RunState run() {
        return run;
    }

    public MapGraph map() {
        return map;
    }

    public MapNode current() {
        return current;
    }

    public Set<MapNode> selectable() {
        return map.selectableFrom(current);
    }

    public void start() {
        nav.showMap();
    }

    public void onNodeSelected(MapNode node) {
        if (!selectable().contains(node)) {
            return;
        }
        current = node;
        run.floor++;
        switch (node.type) {
            case MONSTER:
            case ELITE:
            case BOSS:
                startCombat(node);
                break;
            case REST:
                run.heal((int) Math.ceil(run.maxHp * 0.30));
                afterNode();
                break;
            case TREASURE:
                run.addGold(25 + rng.stream(RngStream.TREASURE).nextInt(26));
                afterNode();
                break;
            case MERCHANT:
            case EVENT:
            default:
                // Placeholder until shop/event screens exist: a small boon.
                run.addGold(15);
                afterNode();
                break;
        }
    }

    private void startCombat(MapNode node) {
        MonsterId enemy = enemyFor(node.type);
        long combatSeed = rng.stream(RngStream.MONSTER).nextLong();
        CombatRequest req = new CombatRequest(
                new ArrayList<>(run.masterDeck), enemy, run.currentHp, run.maxHp, combatSeed);
        nav.showCombat(req, result -> onCombatResult(node, result));
    }

    private MonsterId enemyFor(NodeType type) {
        // Only two enemies exist so far; bosses/elites reuse Jaw Worm for now.
        return type == NodeType.MONSTER ? MonsterId.CULTIST : MonsterId.JAW_WORM;
    }

    private void onCombatResult(MapNode node, CombatResult result) {
        run.currentHp = result.endingHp;
        if (!result.victory || run.isDead()) {
            nav.showGameOver(false);
            return;
        }
        if (node.type == NodeType.BOSS) {
            nav.showGameOver(true);
            return;
        }
        // Combat reward: gold now, a card choice on the reward screen.
        run.addGold(10 + rng.stream(RngStream.MISC).nextInt(11));
        pendingCardChoices = rollCardChoices();
        nav.showReward(node);
    }

    private List<CardId> rollCardChoices() {
        CardId[] pool = {CardId.STRIKE, CardId.DEFEND, CardId.BASH};
        List<CardId> choices = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            choices.add(pool[rng.stream(RngStream.CARD_REWARD).nextInt(pool.length)]);
        }
        return choices;
    }

    public List<CardId> rewardCardChoices() {
        return pendingCardChoices;
    }

    public void takeRewardCard(CardId id) {
        run.addCard(id);
        finishReward();
    }

    public void skipReward() {
        finishReward();
    }

    private void finishReward() {
        pendingCardChoices = new ArrayList<>();
        afterNode();
    }

    private void afterNode() {
        if (run.isDead()) {
            nav.showGameOver(false);
            return;
        }
        nav.showMap();
    }
}

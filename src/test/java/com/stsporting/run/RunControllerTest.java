package com.stsporting.run;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.stsporting.map.MapNode;
import com.stsporting.map.NodeType;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

class RunControllerTest {

    /** Records the last navigation and exposes the combat callback. */
    private static final class FakeNav implements RunNavigator {
        String last;
        Boolean gameOverVictory;
        CombatRequest combatReq;
        Consumer<CombatResult> combatCallback;
        MapNode rewardNode;

        @Override public void showMap() { last = "map"; }

        @Override public void showCombat(CombatRequest request, Consumer<CombatResult> onResult) {
            last = "combat";
            combatReq = request;
            combatCallback = onResult;
        }

        @Override public void showReward(MapNode node) { last = "reward"; rewardNode = node; }

        @Override public void showGameOver(boolean victory) { last = "gameover"; gameOverVictory = victory; }
    }

    private MapNode startNodeOfType(RunController c, NodeType type) {
        // Find a selectable start node, or fabricate a reachable one of the type.
        for (MapNode n : c.selectable()) {
            n.type = type; // tweak for the test scenario
            return n;
        }
        throw new IllegalStateException("no selectable node");
    }

    @Test
    void startShowsMap() {
        FakeNav nav = new FakeNav();
        RunController c = new RunController(nav, 1L);
        c.start();
        assertEquals("map", nav.last);
    }

    @Test
    void monsterNodeLaunchesCombatWithDeckAndHp() {
        FakeNav nav = new FakeNav();
        RunController c = new RunController(nav, 1L);
        MapNode node = startNodeOfType(c, NodeType.MONSTER);
        c.onNodeSelected(node);
        assertEquals("combat", nav.last);
        assertNotNull(nav.combatReq);
        assertEquals(10, nav.combatReq.deck.size());
        assertEquals(80, nav.combatReq.playerHp);
    }

    @Test
    void combatVictoryGoesToRewardAndUpdatesHpAndGold() {
        FakeNav nav = new FakeNav();
        RunController c = new RunController(nav, 1L);
        MapNode node = startNodeOfType(c, NodeType.MONSTER);
        c.onNodeSelected(node);

        nav.combatCallback.accept(new CombatResult(true, 62));
        assertEquals("reward", nav.last);
        assertEquals(62, c.run().currentHp);
        assertTrue(c.run().gold > 99, "gold should increase from the reward");
        assertEquals(3, c.rewardCardChoices().size());
    }

    @Test
    void takingRewardCardAddsItAndReturnsToMap() {
        FakeNav nav = new FakeNav();
        RunController c = new RunController(nav, 1L);
        c.onNodeSelected(startNodeOfType(c, NodeType.MONSTER));
        nav.combatCallback.accept(new CombatResult(true, 70));

        int before = c.run().masterDeck.size();
        c.takeRewardCard(c.rewardCardChoices().get(0));
        assertEquals(before + 1, c.run().masterDeck.size());
        assertEquals("map", nav.last);
    }

    @Test
    void combatDefeatGoesToGameOver() {
        FakeNav nav = new FakeNav();
        RunController c = new RunController(nav, 1L);
        c.onNodeSelected(startNodeOfType(c, NodeType.MONSTER));
        nav.combatCallback.accept(new CombatResult(false, 0));
        assertEquals("gameover", nav.last);
        assertEquals(Boolean.FALSE, nav.gameOverVictory);
    }

    @Test
    void bossVictoryWinsTheRun() {
        FakeNav nav = new FakeNav();
        RunController c = new RunController(nav, 1L);
        c.onNodeSelected(startNodeOfType(c, NodeType.BOSS));
        nav.combatCallback.accept(new CombatResult(true, 40));
        assertEquals("gameover", nav.last);
        assertEquals(Boolean.TRUE, nav.gameOverVictory);
    }

    @Test
    void restNodeHealsAndReturnsToMap() {
        FakeNav nav = new FakeNav();
        RunController c = new RunController(nav, 1L);
        c.run().loseHp(40); // 40/80
        c.onNodeSelected(startNodeOfType(c, NodeType.REST));
        assertEquals("map", nav.last);
        assertEquals(40 + 24, c.run().currentHp); // +30% of 80
    }
}

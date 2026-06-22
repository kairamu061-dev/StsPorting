package com.stsporting.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class MapGeneratorTest {

    private final MapGenerator gen = new MapGenerator();

    @Test
    void hasExpectedRowsAndBoss() {
        MapGraph g = gen.generate(123L);
        assertEquals(MapGenerator.ROWS, g.rowCount());
        assertEquals(1, g.rows.get(g.rowCount() - 1).size());
        assertEquals(NodeType.BOSS, g.boss.type);
        for (MapNode n : g.startNodes()) {
            assertEquals(NodeType.MONSTER, n.type); // opening row is all monsters
        }
    }

    @Test
    void everyStartReachesBossAndAllNodesAreConnected() {
        MapGraph g = gen.generate(777L);
        // BFS from all start nodes; the boss must be reached.
        Set<MapNode> seen = new HashSet<>();
        Deque<MapNode> q = new ArrayDeque<>(g.startNodes());
        seen.addAll(g.startNodes());
        boolean reachedBoss = false;
        while (!q.isEmpty()) {
            MapNode n = q.poll();
            if (n == g.boss) {
                reachedBoss = true;
            }
            for (MapNode nx : n.next) {
                if (seen.add(nx)) {
                    q.add(nx);
                }
            }
        }
        assertTrue(reachedBoss, "boss must be reachable from the start");
        // Every non-start node has at least one incoming edge (no orphans).
        for (List<MapNode> row : g.rows) {
            for (MapNode n : row) {
                if (n.row != 0) {
                    assertFalse(n.prev.isEmpty(), "node " + n.row + "," + n.col + " is orphaned");
                }
            }
        }
    }

    @Test
    void deterministicForSameSeed() {
        MapGraph a = gen.generate(55L);
        MapGraph b = gen.generate(55L);
        assertEquals(a.rowCount(), b.rowCount());
        for (int r = 0; r < a.rowCount(); r++) {
            assertEquals(a.rows.get(r).size(), b.rows.get(r).size(), "row " + r + " size");
            for (int i = 0; i < a.rows.get(r).size(); i++) {
                assertEquals(a.rows.get(r).get(i).type, b.rows.get(r).get(i).type);
                assertEquals(a.rows.get(r).get(i).col, b.rows.get(r).get(i).col);
            }
        }
    }

    @Test
    void selectableFromStartIsBottomRow() {
        MapGraph g = gen.generate(9L);
        assertEquals(new HashSet<>(g.startNodes()), g.selectableFrom(null));
        MapNode first = g.startNodes().get(0);
        assertEquals(new HashSet<>(first.next), g.selectableFrom(first));
        // selecting walks upward only
        for (MapNode nx : first.next) {
            assertSame(first.row + 1, nx.row);
        }
    }
}

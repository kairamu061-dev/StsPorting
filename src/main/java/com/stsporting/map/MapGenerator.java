package com.stsporting.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Deterministic act-map generator. Builds connectivity by walking several
 * bottom-to-top paths (which guarantees every node has a route to the boss),
 * then assigns node types with simple row-based constraints. Same RNG -> same
 * map.
 */
public class MapGenerator {
    public static final int ROWS = 15;          // rows 0..13 normal, row 14 = boss
    public static final int COLS = 7;
    public static final int PATHS = 6;
    private static final int NORMAL_TOP = ROWS - 2; // 13
    private static final int BOSS_ROW = ROWS - 1;   // 14
    private static final int TREASURE_ROW = 8;

    public MapGraph generate(long seed) {
        return generate(new Random(seed));
    }

    public MapGraph generate(Random rng) {
        MapNode[][] grid = new MapNode[ROWS][COLS];

        // Walk paths from the bottom row to the top normal row.
        for (int p = 0; p < PATHS; p++) {
            int col = rng.nextInt(COLS);
            MapNode prev = nodeAt(grid, 0, col);
            for (int row = 1; row <= NORMAL_TOP; row++) {
                col = clamp(col + rng.nextInt(3) - 1, 0, COLS - 1);
                MapNode cur = nodeAt(grid, row, col);
                prev.connect(cur);
                prev = cur;
            }
        }

        // Single boss node, reachable from every top-row node.
        MapNode boss = new MapNode(BOSS_ROW, COLS / 2);
        boss.type = NodeType.BOSS;
        for (MapNode n : rowNodes(grid, NORMAL_TOP)) {
            n.connect(boss);
        }

        assignTypes(grid, rng);

        List<List<MapNode>> rows = new ArrayList<>();
        for (int row = 0; row <= NORMAL_TOP; row++) {
            rows.add(rowNodes(grid, row));
        }
        List<MapNode> bossRow = new ArrayList<>();
        bossRow.add(boss);
        rows.add(bossRow);

        return new MapGraph(rows, boss);
    }

    private void assignTypes(MapNode[][] grid, Random rng) {
        for (int row = 0; row <= NORMAL_TOP; row++) {
            for (MapNode n : rowNodes(grid, row)) {
                n.type = rollType(row, rng);
            }
        }
    }

    private NodeType rollType(int row, Random rng) {
        if (row == 0) {
            return NodeType.MONSTER;        // opening fight
        }
        if (row == NORMAL_TOP) {
            return NodeType.REST;           // campfire before the boss
        }
        if (row == TREASURE_ROW) {
            return NodeType.TREASURE;
        }

        List<NodeType> pool = new ArrayList<>();
        addWeighted(pool, NodeType.MONSTER, 45);
        addWeighted(pool, NodeType.EVENT, 22);
        if (row >= 5) {
            addWeighted(pool, NodeType.ELITE, 16);
            addWeighted(pool, NodeType.MERCHANT, 8);
        }
        if (row >= 6) {
            addWeighted(pool, NodeType.REST, 12);
        }
        return pool.get(rng.nextInt(pool.size()));
    }

    private static void addWeighted(List<NodeType> pool, NodeType type, int weight) {
        for (int i = 0; i < weight; i++) {
            pool.add(type);
        }
    }

    private static MapNode nodeAt(MapNode[][] grid, int row, int col) {
        if (grid[row][col] == null) {
            grid[row][col] = new MapNode(row, col);
        }
        return grid[row][col];
    }

    private static List<MapNode> rowNodes(MapNode[][] grid, int row) {
        List<MapNode> list = new ArrayList<>();
        for (int col = 0; col < COLS; col++) {
            if (grid[row][col] != null) {
                list.add(grid[row][col]);
            }
        }
        return list;
    }

    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }
}

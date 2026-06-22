package com.stsporting.map;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** A generated act map: rows of nodes (row 0 at the bottom) and the boss. */
public class MapGraph {
    public final List<List<MapNode>> rows;
    public final MapNode boss;

    public MapGraph(List<List<MapNode>> rows, MapNode boss) {
        this.rows = rows;
        this.boss = boss;
    }

    public int rowCount() {
        return rows.size();
    }

    public List<MapNode> startNodes() {
        return rows.get(0);
    }

    /** Nodes selectable next: the start row if not yet on the map, else neighbours up. */
    public Set<MapNode> selectableFrom(MapNode current) {
        if (current == null) {
            return new HashSet<>(startNodes());
        }
        return new HashSet<>(current.next);
    }
}

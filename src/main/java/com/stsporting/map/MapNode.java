package com.stsporting.map;

import java.util.ArrayList;
import java.util.List;

/** A single node in the act map, with edges up to the next row. */
public class MapNode {
    public final int row;
    public final int col;
    public NodeType type;
    public final List<MapNode> next = new ArrayList<>();
    public final List<MapNode> prev = new ArrayList<>();

    public MapNode(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /** Add an upward edge to {@code to} (idempotent), keeping prev in sync. */
    public void connect(MapNode to) {
        if (!next.contains(to)) {
            next.add(to);
            to.prev.add(this);
        }
    }
}

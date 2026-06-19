package com.stsporting.combat.input;

/** States of the combat hand-input machine. */
public enum InputState {
    /** Nothing hovered or held. */
    IDLE,
    /** Cursor is over a card (rendered enlarged). */
    HOVER,
    /** Holding a non-targeted card; release above the threshold plays it. */
    DRAGGING,
    /** Holding a single-target card; release on an enemy plays it (arrow shown). */
    TARGETING
}

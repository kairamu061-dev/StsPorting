package com.stsporting.combat.input;

import com.stsporting.combat.CombatState;
import com.stsporting.combat.action.ActionManager;
import com.stsporting.combat.card.AbstractCard;
import com.stsporting.combat.card.CardTarget;
import com.stsporting.combat.card.PlayCardFlow;
import com.stsporting.combat.creature.Creature;
import java.util.function.BooleanSupplier;

/**
 * Hand-input state machine (IDLE/HOVER/DRAGGING/TARGETING) reproducing the
 * original's feel: hover lifts a card, dragging a non-targeted card up past a
 * threshold plays it (releasing below cancels), and a single-target card is
 * dragged onto an enemy to play it. GL-independent so the transitions are
 * unit-testable; the screen feeds virtual-space pointer events and reads back
 * state for rendering.
 */
public class CombatInputController {
    private final CombatState state;
    private final ActionManager mgr;
    private final HandLayout layout;
    private final TargetResolver targets;
    private final float playThresholdY;
    private final BooleanSupplier inputAllowed;

    private InputState inputState = InputState.IDLE;
    private AbstractCard hovered;
    private AbstractCard dragCard;
    private float dragX;
    private float dragY;
    private boolean lastPlayRejected;

    public CombatInputController(CombatState state, ActionManager mgr, HandLayout layout,
                                 TargetResolver targets, float playThresholdY,
                                 BooleanSupplier inputAllowed) {
        this.state = state;
        this.mgr = mgr;
        this.layout = layout;
        this.targets = targets;
        this.playThresholdY = playThresholdY;
        this.inputAllowed = inputAllowed;
    }

    public InputState state() {
        return inputState;
    }

    public AbstractCard hoveredCard() {
        return hovered;
    }

    public AbstractCard draggingCard() {
        return dragCard;
    }

    public float dragX() {
        return dragX;
    }

    public float dragY() {
        return dragY;
    }

    public boolean lastPlayRejected() {
        return lastPlayRejected;
    }

    private boolean allowed() {
        return inputAllowed.getAsBoolean();
    }

    private boolean playable(AbstractCard c) {
        return c.isPlayable() && state.energy >= c.cost();
    }

    private AbstractCard cardAt(float vx, float vy) {
        int n = state.hand.size();
        for (int i = n - 1; i >= 0; i--) { // front-most first
            if (layout.hitRect(i, n).contains(vx, vy)) {
                return state.hand.get(i);
            }
        }
        return null;
    }

    public void onMouseMoved(float vx, float vy) {
        if (inputState == InputState.DRAGGING || inputState == InputState.TARGETING) {
            dragX = vx;
            dragY = vy;
            return;
        }
        if (!allowed()) {
            hovered = null;
            inputState = InputState.IDLE;
            return;
        }
        hovered = cardAt(vx, vy);
        inputState = (hovered != null) ? InputState.HOVER : InputState.IDLE;
    }

    /** @return true if a card was picked up. */
    public boolean onTouchDown(float vx, float vy) {
        if (!allowed()) {
            return false;
        }
        AbstractCard c = cardAt(vx, vy);
        if (c == null) {
            return false;
        }
        dragCard = c;
        dragX = vx;
        dragY = vy;
        lastPlayRejected = false;
        inputState = (c.target == CardTarget.ENEMY) ? InputState.TARGETING : InputState.DRAGGING;
        return true;
    }

    /** @return true if a card was played. */
    public boolean onTouchUp(float vx, float vy) {
        boolean played = false;
        if (inputState == InputState.DRAGGING && dragCard != null) {
            if (vy >= playThresholdY && playable(dragCard)) {
                played = PlayCardFlow.resolve(mgr, dragCard, null);
            }
            lastPlayRejected = !played;
        } else if (inputState == InputState.TARGETING && dragCard != null) {
            Creature enemy = targets.enemyAt(vx, vy);
            if (enemy != null && !enemy.isDead() && playable(dragCard)) {
                played = PlayCardFlow.resolve(mgr, dragCard, enemy);
            }
            lastPlayRejected = !played;
        }
        dragCard = null;
        hovered = null;
        inputState = InputState.IDLE;
        return played;
    }
}

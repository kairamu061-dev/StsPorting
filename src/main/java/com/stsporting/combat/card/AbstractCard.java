package com.stsporting.combat.card;

import com.stsporting.combat.action.ActionManager;
import com.stsporting.combat.creature.Creature;

/**
 * A card's in-combat behaviour and mutable state. Content sub-types implement
 * {@link #use} to queue their effect actions; this base provides the card
 * lifecycle (type, cost, target, upgrade, playability).
 */
public abstract class AbstractCard {
    public final String id;
    public String name;
    public CardType type;
    public CardTarget target;

    public int baseCost;
    /** Cost for the current turn (reset to baseCost each turn; 0 for temp-free). */
    public int costForTurn;
    /** One-shot free play (consumed when played). */
    public boolean freeToPlayOnce;
    /** Whether the card is exhausted (removed from the combat) after use. */
    public boolean exhaust;
    public boolean upgraded;

    protected AbstractCard(String id, String name, CardType type, int cost, CardTarget target) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.target = target;
        this.baseCost = cost;
        this.costForTurn = cost;
    }

    /** Queue this card's effects onto the action manager. */
    public abstract void use(Creature target, ActionManager mgr);

    /** Apply the upgraded version. Override to adjust values/cost. */
    public void upgrade() {
        upgraded = true;
    }

    public boolean canUpgrade() {
        return !upgraded;
    }

    /** STATUS/CURSE cards are not normally playable. */
    public boolean isPlayable() {
        return type != CardType.STATUS && type != CardType.CURSE;
    }

    /** Effective energy cost to play this turn. */
    public int cost() {
        return freeToPlayOnce ? 0 : costForTurn;
    }

    /** Reset per-turn cost mutations (called at turn start). */
    public void resetCostForTurn() {
        costForTurn = baseCost;
    }
}

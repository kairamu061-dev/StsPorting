package com.stsporting.run;

import com.stsporting.content.cards.CardId;
import com.stsporting.content.monsters.MonsterId;
import java.util.List;

/** What the run hands to a combat: the deck, the enemy, player HP and a seed. */
public class CombatRequest {
    public final List<CardId> deck;
    public final MonsterId enemy;
    public final int playerHp;
    public final int playerMaxHp;
    public final long combatSeed;

    public CombatRequest(List<CardId> deck, MonsterId enemy, int playerHp, int playerMaxHp, long combatSeed) {
        this.deck = deck;
        this.enemy = enemy;
        this.playerHp = playerHp;
        this.playerMaxHp = playerMaxHp;
        this.combatSeed = combatSeed;
    }
}

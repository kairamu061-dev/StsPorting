package com.stsporting.content.cards;

import com.stsporting.combat.card.AbstractCard;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/** Registry of card factories and the Ironclad starter deck. */
public final class CardLibrary {
    private static final Map<CardId, Supplier<AbstractCard>> FACTORIES = new EnumMap<>(CardId.class);

    static {
        FACTORIES.put(CardId.STRIKE, Strike::new);
        FACTORIES.put(CardId.DEFEND, Defend::new);
        FACTORIES.put(CardId.BASH, Bash::new);
    }

    private CardLibrary() {
    }

    public static AbstractCard newCard(CardId id) {
        Supplier<AbstractCard> f = FACTORIES.get(id);
        if (f == null) {
            throw new IllegalArgumentException("Unregistered card: " + id);
        }
        return f.get();
    }

    /** Ironclad starting deck: 5 Strike, 4 Defend, 1 Bash. */
    public static List<AbstractCard> starterDeck() {
        List<AbstractCard> deck = new ArrayList<>();
        for (CardId id : starterDeckIds()) {
            deck.add(newCard(id));
        }
        return deck;
    }

    /** Ironclad starting deck as ids (the run layer's master deck). */
    public static List<CardId> starterDeckIds() {
        List<CardId> deck = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            deck.add(CardId.STRIKE);
        }
        for (int i = 0; i < 4; i++) {
            deck.add(CardId.DEFEND);
        }
        deck.add(CardId.BASH);
        return deck;
    }
}

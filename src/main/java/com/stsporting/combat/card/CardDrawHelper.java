package com.stsporting.combat.card;

import com.stsporting.combat.CombatState;
import java.util.Collections;

/**
 * Draw-pile mechanics: drawing into hand, reshuffling the discard pile into the
 * draw pile when empty, and respecting the hand-size cap.
 */
public final class CardDrawHelper {
    private CardDrawHelper() {
    }

    /** Draw up to n cards; stops early on hand cap or when no cards remain. */
    public static void draw(CombatState s, int n) {
        for (int i = 0; i < n; i++) {
            if (s.hand.size() >= CombatState.MAX_HAND) {
                return;
            }
            if (s.drawPile.isEmpty()) {
                if (s.discardPile.isEmpty()) {
                    return; // nothing left to draw
                }
                reshuffleDiscardIntoDraw(s);
            }
            AbstractCard top = s.drawPile.remove(s.drawPile.size() - 1);
            s.hand.add(top);
        }
    }

    public static void reshuffleDiscardIntoDraw(CombatState s) {
        s.drawPile.addAll(s.discardPile);
        s.discardPile.clear();
        Collections.shuffle(s.drawPile, s.rng);
    }
}

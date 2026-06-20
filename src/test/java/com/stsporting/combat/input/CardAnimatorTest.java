package com.stsporting.combat.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.stsporting.combat.card.AbstractCard;
import com.stsporting.content.cards.CardId;
import com.stsporting.content.cards.CardLibrary;
import java.util.List;
import org.junit.jupiter.api.Test;

class CardAnimatorTest {

    private final HandLayout layout = new HandLayout(1000, 200, 100, 200, 120, 1000, 0, 0);

    @Test
    void newCardEntersFromSpawnAndMovesTowardSlot() {
        CardAnimator anim = new CardAnimator(0, 0);
        AbstractCard c = CardLibrary.newCard(CardId.STRIKE);

        anim.update(List.of(c), layout, 0.016f); // ~one frame
        // One step: started at (0,0), eased toward (1000,200) but not arrived.
        assertTrue(anim.x(c) > 0f && anim.x(c) < 1000f);
        assertTrue(anim.y(c) > 0f && anim.y(c) < 200f);
    }

    @Test
    void easesToSlotOverTime() {
        CardAnimator anim = new CardAnimator(0, 0);
        AbstractCard c = CardLibrary.newCard(CardId.STRIKE);
        for (int i = 0; i < 300; i++) {
            anim.update(List.of(c), layout, 0.1f);
        }
        assertEquals(1000f, anim.x(c), 1f);
        assertEquals(200f, anim.y(c), 1f);
    }

    @Test
    void cardLeavingHandIsUntracked() {
        CardAnimator anim = new CardAnimator(0, 0);
        AbstractCard c = CardLibrary.newCard(CardId.STRIKE);
        anim.update(List.of(c), layout, 0.1f);
        assertTrue(anim.has(c));
        anim.update(List.of(), layout, 0.1f);
        assertFalse(anim.has(c));
    }
}

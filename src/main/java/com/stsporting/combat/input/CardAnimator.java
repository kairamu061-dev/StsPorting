package com.stsporting.combat.input;

import com.stsporting.combat.card.AbstractCard;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Smoothly eases each hand card toward its {@link HandLayout} slot. New cards
 * enter from a spawn point (the draw pile) so draws animate in; cards that leave
 * the hand stop being tracked. Pure math (frame-rate-independent lerp), so the
 * easing is unit-testable without rendering.
 */
public class CardAnimator {
    private static final class Anim {
        float x;
        float y;
    }

    private final Map<AbstractCard, Anim> anims = new HashMap<>();
    private final float spawnX;
    private final float spawnY;
    private final float speed;

    public CardAnimator(float spawnX, float spawnY) {
        this(spawnX, spawnY, 12f);
    }

    public CardAnimator(float spawnX, float spawnY, float speed) {
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.speed = speed;
    }

    public void update(List<AbstractCard> hand, HandLayout layout, float delta) {
        anims.keySet().removeIf(c -> !hand.contains(c));

        int n = hand.size();
        float t = Math.min(1f, speed * delta);
        for (int i = 0; i < n; i++) {
            AbstractCard c = hand.get(i);
            Pose target = layout.poseFor(i, n);
            Anim a = anims.get(c);
            if (a == null) {
                a = new Anim();
                a.x = spawnX;
                a.y = spawnY;
                anims.put(c, a);
            }
            a.x += (target.x - a.x) * t;
            a.y += (target.y - a.y) * t;
        }
    }

    public boolean has(AbstractCard c) {
        return anims.containsKey(c);
    }

    public float x(AbstractCard c) {
        Anim a = anims.get(c);
        return a != null ? a.x : spawnX;
    }

    public float y(AbstractCard c) {
        Anim a = anims.get(c);
        return a != null ? a.y : spawnY;
    }
}

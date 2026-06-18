package com.stsporting.combat.action;

import com.stsporting.combat.DamageInfo;
import com.stsporting.combat.DamageType;
import com.stsporting.combat.creature.Creature;
import com.stsporting.combat.power.AbstractPower;
import com.stsporting.combat.power.PowerHooks;
import java.util.ArrayList;

/**
 * Deals damage following the fixed resolution order:
 * attacker give-modifiers (Strength +, Weak x0.75) -> target receive-modifiers
 * (Vulnerable x1.5) -> block absorption -> HP -> on-attacked triggers (Thorns).
 * On-attacked reactions are queued via {@code addToTop} so they resolve next.
 */
public class DamageAction extends GameAction {
    private final Creature target;
    private final int base;
    private final Creature source;
    private final DamageType type;
    private boolean applied;

    public DamageAction(Creature target, int base, Creature source, DamageType type) {
        this.target = target;
        this.base = base;
        this.source = source;
        this.type = type;
        this.duration = 0.1f;
        this.startDuration = 0.1f;
    }

    @Override
    public void update(float delta) {
        if (!applied) {
            applied = true;
            apply();
        }
        tickDuration(delta);
    }

    private void apply() {
        if (target.isDead()) {
            return;
        }
        int dmg = base;
        if (type == DamageType.ATTACK && source != null) {
            dmg = PowerHooks.applyDamageGive(source, dmg, type);
        }
        if (type == DamageType.ATTACK) {
            dmg = PowerHooks.applyDamageReceive(target, dmg, type);
        }
        if (dmg < 0) {
            dmg = 0;
        }

        int overflow = Math.max(0, dmg - target.block);
        target.block = Math.max(0, target.block - dmg);
        target.currentHp -= overflow;
        if (target.currentHp < 0) {
            target.currentHp = 0;
        }

        // On-attacked hooks (e.g. Thorns). Copy to avoid concurrent modification.
        DamageInfo info = new DamageInfo(source, base, type);
        for (AbstractPower p : new ArrayList<>(target.powers)) {
            p.onAttacked(info, overflow);
        }
    }
}

package com.stsporting.content.monsters;

import com.stsporting.combat.DamageType;
import com.stsporting.combat.action.ApplyPowerAction;
import com.stsporting.combat.action.DamageAction;
import com.stsporting.combat.action.GainBlockAction;
import com.stsporting.combat.creature.enemy.AbstractMonster;
import com.stsporting.combat.creature.enemy.EnemyMove;
import com.stsporting.combat.creature.enemy.IntentType;
import com.stsporting.combat.creature.enemy.Moves;
import com.stsporting.combat.power.StrengthPower;
import java.util.List;

/**
 * Jaw Worm: mixes a heavy attack (Chomp), an attack+block (Thrash) and a
 * self-buff+block (Bellow). Uses weighted selection with consecutive limits.
 */
public class JawWorm extends AbstractMonster {
    public JawWorm() {
        super("jaw_worm", "Jaw Worm", 40, 44);
    }

    @Override
    public List<EnemyMove> moves() {
        EnemyMove chomp = Moves.attack("chomp", 11, 2, 1);
        EnemyMove thrash = new EnemyMove("thrash", IntentType.ATTACK_DEFEND, 7, 1, 3, 0,
                (mgr, self, player) -> {
                    mgr.addToBottom(new DamageAction(player, 7, self, DamageType.ATTACK));
                    mgr.addToBottom(new GainBlockAction(self, 5));
                });
        EnemyMove bellow = new EnemyMove("bellow", IntentType.BUFF, 0, 0, 2, 1,
                (mgr, self, player) -> {
                    mgr.addToBottom(new ApplyPowerAction(self, new StrengthPower(), 3));
                    mgr.addToBottom(new GainBlockAction(self, 6));
                });
        return List.of(chomp, thrash, bellow);
    }
}

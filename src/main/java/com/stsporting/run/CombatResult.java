package com.stsporting.run;

/** What a combat reports back to the run: win/lose and the player's ending HP. */
public class CombatResult {
    public final boolean victory;
    public final int endingHp;

    public CombatResult(boolean victory, int endingHp) {
        this.victory = victory;
        this.endingHp = endingHp;
    }
}

# monsters（content）設計

## 技術選定

| 技術 | 用途 | 選定理由 |
|------|------|----------|
| combat/enemy.AbstractMonster 継承 | 各敵 | 仕組みは combat、具体は content |
| MonsterId enum / MonsterLibrary | 識別・生成 | タイプセーフ参照と登録 |
| EncounterTable（データ） | 編成 | ノード種別×幕の編成抽選 |

## アーキテクチャ

```
com.stsporting.content.monsters
├── MonsterId (enum)    … CULTIST, JAW_WORM, GREMLIN_NOB, LAGAVULIN, THE_GUARDIAN, ...
├── MonsterLibrary      … MonsterId -> Supplier<AbstractMonster>
├── act1/
│   ├── Cultist / JawWorm / (small monsters)
│   ├── GremlinNob / Lagavulin
│   └── TheGuardian
├── moves/              … 共通行動ヘルパ（攻撃/防御/バフ/デバフの EnemyMove 生成）
└── encounters/
    └── EncounterTable  … act1_weak/strong/elite/boss と rollEncounter
```

## 実装例

```java
class Cultist extends AbstractMonster {
    Cultist() { super(MonsterId.CULTIST, "カルティスト", /*hp*/ 48, 54); }
    List<EnemyMove> moves() {
        return List.of(
            Moves.buff("RITUAL", () -> new ApplyPowerAction(this, new RitualPower(), 3)),
            Moves.attack("DARK_STRIKE", 6)
        );
    }
    void rollNextMove(Random rng) {
        nextMove = (turnCount == 0) ? move("RITUAL") : move("DARK_STRIKE");
    }
}

class GremlinNob extends AbstractMonster {
    GremlinNob() { super(MonsterId.GREMLIN_NOB, "グレムリンノブ", 82, 86); }
    void initialize(Random rng) {
        super.initialize(rng);
        addPower(new EnragePower(2)); // プレイヤーの onUseCard(SKILL) で筋力増
    }
    List<EnemyMove> moves() {
        return List.of(
            Moves.buff("BELLOW", () -> new ApplyPowerAction(this, new StrengthPower(), 2)),
            Moves.attack("RUSH", 14, /*maxConsecutive*/ 2),
            Moves.attack("SKULL_BASH", 6) // +脆弱
        );
    }
}

class TheGuardian extends AbstractMonster {
    // モードシフト: 受けた累計ダメージが閾値超で DEFENSIVE へ。解除で OFFENSIVE。
    int modeShiftThreshold = 30, damageTakenThisCycle = 0;
    void onAttacked(DamageInfo info, int dmg) {
        damageTakenThisCycle += dmg;
        if (mode == OFFENSIVE && damageTakenThisCycle >= modeShiftThreshold) switchToDefensive();
    }
}
```

> `EnragePower` は敵に付くが、プレイヤーの `onUseCard` を購読する形にする（powers のフックを敵→プレイヤーイベントに接続する設計を combat/powers と擦り合わせ）。

## エンカウンター

```java
class EncounterTable {
    EncounterDef rollEncounter(NodeType type, int act, Random rng) {
        // type=MONSTER: floor が浅ければ weak、深ければ strong から抽選
        // type=ELITE: elite プールから / type=BOSS: boss プールから
    }
}
EncounterDef { List<MonsterId> monsters; } // 配置順
```

## インターフェース

```java
class MonsterLibrary {
    static void register();
    static AbstractMonster newMonster(MonsterId id);
}
```

## 依存関係

| ライブラリ / サービス | 用途 |
|-----------------------|------|
| combat/enemy（AbstractMonster/EnemyMove/Intent） | 敵の仕組み |
| combat/action-queue・powers | 行動効果・敵バフ・Enrage 等 |
| content（ContentRegistry） | 登録・参照 |
| map / run | エンカウンター割り当て・戦闘起動 |

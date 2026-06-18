# cards（content）設計

## 技術選定

| 技術 | 用途 | 選定理由 |
|------|------|----------|
| combat/card.AbstractCard 継承 | 各カード | 仕組みは combat、効果は content という分担 |
| CardId enum | 識別子 | タイプセーフな参照 |
| ファクトリ登録（CardLibrary） | 生成 | ID から新規インスタンスを生成し ContentRegistry に登録 |

## アーキテクチャ

```
com.stsporting.content.cards
├── CardId (enum)       … STRIKE, DEFEND, BASH, ANGER, CLEAVE, IRON_WAVE, ...
├── CardLibrary         … CardId -> Supplier<AbstractCard> 登録、newCard(id)
├── ironclad/
│   ├── Strike / Defend / Bash
│   ├── Anger / Cleave / IronWave / BodySlam / TwinStrike / PommelStrike
│   ├── Flex / Inflame / Metallicize ...
└── pools/
    └── CardPools       … レアリティ別プール（報酬抽選用）
```

- 各カードクラスは `AbstractCard` を継承し、コンストラクタで id/name/type/cost/target/rarity を設定、`use()` と `upgrade()` を実装。
- `CardLibrary.register()` を起動時に呼び、`ContentRegistry.newCard(id)` から取得可能にする。

## データ構造 / 実装例

```java
class Strike extends AbstractCard {
    Strike() { super(CardId.STRIKE, "ストライク", ATTACK, /*cost*/1, ENEMY, BASIC); baseDamage = 6; }
    void use(Player p, Creature t) {
        mgr.addToBottom(new DamageAction(t, damage(), p, ATTACK));
    }
    void upgrade() { upgraded = true; baseDamage = 9; }
}

class Bash extends AbstractCard {
    Bash() { super(CardId.BASH, "バッシュ", ATTACK, 2, ENEMY, BASIC); baseDamage = 8; }
    void use(Player p, Creature t) {
        mgr.addToBottom(new DamageAction(t, damage(), p, ATTACK));
        mgr.addToBottom(new ApplyPowerAction(t, new VulnerablePower(), upgraded?3:2));
    }
    void upgrade() { upgraded = true; baseDamage = 10; }
}

class Inflame extends AbstractCard {   // POWER
    Inflame() { super(CardId.INFLAME, "インフレイム", POWER, 1, SELF, UNCOMMON); }
    void use(Player p, Creature t) {
        mgr.addToBottom(new ApplyPowerAction(p, new StrengthPower(), upgraded?3:2));
    }
}
```

`damage()` は `baseDamage` を起点に combat 側の修正（筋力等）は DamageAction が行うため、ここでは base のみ渡す。

## レアリティ別プール

```java
class CardPools {
    List<CardId> common, uncommon, rare;   // 報酬対象（BASIC は除外）
    CardId rollReward(Random rng);          // レアリティ確率→該当プールから抽選
}
```

## インターフェース

```java
class CardLibrary {
    static void register();                 // 全カードを登録
    static AbstractCard newCard(CardId id);
}
```

## 依存関係

| ライブラリ / サービス | 用途 |
|-----------------------|------|
| combat/cards（AbstractCard） | カードの仕組み |
| combat/action-queue（DamageAction/GainBlockAction/DrawAction） | 効果の実体 |
| combat/powers（Strength/Vulnerable/Metallicize） | 状態異常付与 |
| content（ContentRegistry） | 登録・参照 |
| run（RewardBuilder） | 報酬プールの利用 |

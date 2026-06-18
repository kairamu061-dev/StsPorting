# cards 設計

## 技術選定

| 技術 | 用途 | 選定理由 |
|------|------|----------|
| Java / 抽象クラス AbstractCard | カードモデル | content がサブクラスで効果を実装。原作の AbstractCard を踏襲 |
| List ベースの piles | 所在管理 | 順序を保持（ドロー順・表示順）。シャッフルは RunRng |
| action-queue 連携 | プレイ処理 | use() が GameAction を積む。即時適用しない |

## アーキテクチャ

```
com.stsporting.combat.card
├── AbstractCard        … 種別/コスト/対象/可変状態。use()/canUse()/upgrade()
├── CardType (enum)     … ATTACK, SKILL, POWER, STATUS, CURSE
├── CardTarget (enum)   … ENEMY, ALL_ENEMY, SELF, NONE, SELF_AND_ENEMY
├── CardPiles           … drawPile/hand/discardPile/exhaustPile と移動API
├── CardDrawHelper      … draw(n)/shuffleDiscardIntoDraw（RunRng）
└── play/
    └── PlayCardFlow    … canPlay→spendEnergy→use→postUse(move/exhaust/power)
```

- `AbstractCard` は戦闘内可変状態（`costForTurn`、`freeToPlayOnce`、`upgraded`、一時効果）を持つ。`use(player, target)` で content 実装が `mgr.addToBottom(new DamageAction(...))` 等を積む。
- `PlayCardFlow` は action-queue の CardQueueItem 取り出し時に呼ばれ、可否判定〜使用後移動を一括処理。

## データ構造

```java
abstract class AbstractCard {
    CardId id;
    String name;
    CardType type;
    CardTarget target;
    int baseCost;
    int costForTurn;       // 一時変更（0コスト等）。ターンで baseCost に戻す
    boolean freeToPlayOnce;// 1回だけ0コスト
    boolean exhaust;       // 使用後消滅
    boolean upgraded;

    abstract void use(Player p, Creature target); // 効果アクションを積む
    boolean canUse(Player p, Creature target);     // プレイ可能条件
    void upgrade();                                  // 効果/コスト差分
    boolean isPlayable();                            // 呪い/状態異常は false 等
}

class CardPiles {
    List<AbstractCard> drawPile, hand, discardPile, exhaustPile;
    void moveToDiscard(AbstractCard c);
    void moveToExhaust(AbstractCard c);   // onExhaust フック
    void moveToHand(AbstractCard c);
}
```

## プレイフロー擬似コード

```java
void resolveCardPlay(CardQueueItem item) {
    AbstractCard c = item.card;
    if (!c.isPlayable() || !c.canUse(player, item.target) || !energy.canAfford(c)) {
        returnToHand(c); flashInvalid(c); return;
    }
    int cost = (c.freeToPlayOnce ? 0 : c.costForTurn);
    if (cost == X_COST) energy.spendAll(); else energy.spend(cost);
    piles.hand.remove(c);
    fireOnUseCard(c);                 // レリック/パワー: カードプレイ時
    c.use(player, item.target);       // 効果アクションを積む（content）
    // postUse は効果アクションの後に積む後処理アクションで実施
    mgr.addToBottom(new PostPlayAction(c)); // POWER→消費 / exhaust→exhaustPile / else→discard
}
```

> `PostPlayAction` を効果アクションの後に積むことで、「効果解決 → カード移動」の順序を原作と一致させる。

## インターフェース

```java
class CardDrawHelper {
    void draw(int n);                  // 手札上限10、山札切れシャッフル
    void shuffleDiscardIntoDraw(Random rng);
}
class PlayCardFlow {
    void resolve(CardQueueItem item);  // action-queue から呼ばれる
}
```

## 依存関係

| ライブラリ / サービス | 用途 |
|-----------------------|------|
| action-queue | プレイ要求の解決・効果アクション投入・PostPlayAction |
| turn-flow（EnergyManager） | コスト消費・X コスト |
| powers | カードプレイ時フック・コスト/効果の修正 |
| content/cards | 各カードの use()/数値/アップグレード |
| effects | 移動/無効フィードバックの演出（論理はここ） |
| run（RunRng） | シャッフルの決定的乱数 |

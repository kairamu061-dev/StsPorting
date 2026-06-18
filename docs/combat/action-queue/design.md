# action-queue 設計

## 技術選定

| 技術 | 用途 | 選定理由 |
|------|------|----------|
| Java / 自作キュー（ArrayDeque ベース） | 解決機構 | 原作 GameActionManager を踏襲。先頭挿入/末尾追加を O(1) で扱える |
| delta 駆動 update | 演出待ち | フレームレート非依存に duration を消化 |
| RunRng（combat 系統） | 決定的乱数 | シャッフル/AI と整合し、テスト再現性を確保 |

## アーキテクチャ

```
com.stsporting.combat.action
├── GameAction          … 抽象基底。update(delta)/isDone/duration
├── ActionManager       … 解決ループ。currentAction + Deque<GameAction>
├── common/             … 基本アクション群
│   ├── DamageAction
│   ├── GainBlockAction
│   ├── DrawAction / DiscardAction / ExhaustAction
│   ├── ApplyPowerAction / ReducePowerAction
│   └── WaitAction（純粋な演出待ち）
└── CardQueueItem       … プレイ要求（card + target）→ アクション列へ展開
```

- `ActionManager` は `CombatState` への参照を持ち、各アクションは `manager`/`state` を通じて盤面を更新し、必要に応じ `manager.addToTop(...)` で割り込む。
- `CombatScreen.update(delta)` が毎フレーム `actionManager.update(delta)` を呼ぶ。キューがアイドルのときのみプレイヤー入力（カードプレイ/ターン終了）を受け付ける。

## データ構造

```java
abstract class GameAction {
    protected float duration;       // 残り演出時間（0 で即時系）
    protected float startDuration;
    public boolean isDone;
    protected ActionManager mgr;
    protected CombatState state;

    public abstract void update(float delta);

    // ヘルパ: 演出待ちを進め、満了で isDone=true にする即時/待ちの定型
    protected void tickDuration(float delta) {
        duration -= delta;
        if (duration <= 0) isDone = true;
    }
}

class ActionManager {
    private GameAction current;
    private final Deque<GameAction> queue = new ArrayDeque<>();
    private final Deque<CardQueueItem> cardQueue = new ArrayDeque<>();

    void addToBottom(GameAction a);  // queue.addLast
    void addToTop(GameAction a);     // queue.addFirst
    void queueCard(CardQueueItem c); // プレイ要求を投入
    void update(float delta);        // 解決ループ（下記）
    boolean isIdle();                // current==null && queue空 && cardQueue空
}
```

### update(delta) の擬似コード

```
void update(delta):
    if current == null:
        if !queue.isEmpty():
            current = queue.pollFirst()
        elif !cardQueue.isEmpty():
            CardQueueItem c = cardQueue.pollFirst()
            playCard(c)            // コスト消費 + card.use() が各効果を addToBottom/addToTop
            return                 // 次フレームで積まれた効果を解決
        else:
            return                 // アイドル
    current.update(delta)
    if current.isDone:
        current = null             // 次フレームで次のアクション（割り込み優先）
```

> ポイント: `current.isDone` 後に即座に次を取り出さず 1 段階で null に戻すことで、アクションが積んだ割り込みが必ず「次に」先頭から取り出される。原作の解決順に一致させる。

### DamageAction の解決順（親規約の具体化）

```
update(delta):
    if !applied:
        int dmg = base
        dmg = applyStrength(source, dmg)        // + 筋力
        dmg = applyWeak(source, dmg)            // 弱体 ×0.75 (切り捨て)
        dmg = applyVulnerable(target, dmg)      // 脆弱 ×1.5 (切り捨て)
        int over = max(0, dmg - target.block)
        target.block = max(0, target.block - dmg)
        target.currentHp -= over
        applied = true
        triggerOnAttacked(target, dmg)          // とげ等 → addToTop で割り込み
        startHitVfx(target)                     // effects へ合図、duration セット
    tickDuration(delta)
```

## インターフェース

```java
// 他サブ項目から使う主な口
manager.addToBottom(new DamageAction(target, 6, player));
manager.addToTop(new ApplyPowerAction(target, new VulnerablePower(), 2));
manager.queueCard(new CardQueueItem(card, targetEnemy));
boolean ready = manager.isIdle(); // 入力受付可否
```

## 依存関係

| ライブラリ / サービス | 用途 |
|-----------------------|------|
| combat 親（CombatState/Creature/ダメージ計算ユーティリティ） | 盤面更新の対象 |
| powers サブ項目 | 修正値（筋力/弱体/脆弱）・被弾フックの提供 |
| effects サブ項目 | 演出開始の合図・duration の根拠 |
| run（RunRng） | シャッフル/AI の決定的乱数 |

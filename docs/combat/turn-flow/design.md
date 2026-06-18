# turn-flow 設計

## 技術選定

| 技術 | 用途 | 選定理由 |
|------|------|----------|
| Java / 状態 enum | フェーズ管理 | 単純で決定的。キューアイドルを条件に遷移 |
| action-queue | 定常処理の実行 | 全処理をアクション化し演出と順序を一致 |
| RunRng（combat 系統） | シャッフル | 決定的 |

## アーキテクチャ

```
com.stsporting.combat.flow
├── TurnController      … フェーズ状態機械。CombatScreen から駆動
├── Phase (enum)        … BEGIN, PLAYER, END_PLAYER, ENEMY, FINISHED
├── EnergyManager       … 最大/現在エネルギー・回復・消費・X
└── flow actions/       … 定常処理アクション
    ├── ShuffleDrawPileAction
    ├── DrawAction（action-queue の DrawAction を利用）
    ├── GainEnergyAction / ResetBlockAction
    ├── TurnStartPowerAction / TurnEndPowerAction（powers のフック発火）
    └── EnemyTurnAction（各敵 takeTurn を順に積む）
```

- `TurnController.update()` は `actionManager.isIdle()` のときだけフェーズを進める。各フェーズ突入時に必要な定常アクションを `addToBottom` で積む。
- フック（atTurnStart 等）は `powers`/`relics` が購読する。TurnController は対象クリーチャーのパワー集合に対しフックアクションを生成して積む。

## フェーズ遷移ロジック

```java
void update(float delta) {
    if (!actionManager.isIdle()) return;   // 演出/解決中は待つ
    switch (phase) {
        case BEGIN:
            queueBattleStart();            // shuffle, placeEnemies(別途), onBattleStart, initialDraw
            phase = PLAYER; firstPlayerEntry = true; break;
        case PLAYER:
            if (firstPlayerEntry || returningFromEnemy) {
                queuePlayerTurnStart();     // resetBlock, atTurnStart, gainEnergy(max), draw
                clearEntryFlags();
            }
            // 以降は input がカードプレイ/ターン終了を駆動。ターン終了要求で END_PLAYER へ
            break;
        case END_PLAYER:
            queuePlayerTurnEnd();           // atTurnEnd, handleHand(discard/retain)
            phase = ENEMY; break;
        case ENEMY:
            queueEnemyTurn();               // 各敵: atTurnStart→takeTurn→atTurnEnd, 次インテント
            if (checkWinLose()) { phase = FINISHED; }
            else { returningFromEnemy = true; phase = PLAYER; }
            break;
        case FINISHED:
            break;
    }
    if (phase != FINISHED) checkWinLose();  // 各遷移点でも判定
}
```

> `END_PLAYER` への遷移は input からの「ターン終了」要求でフラグを立て、次の isIdle 時に処理する。

## データ構造

```java
class EnergyManager {
    int max = 3;
    int current;
    void onPlayerTurnStart() { current = max; }
    boolean spend(int cost) { if (current < cost) return false; current -= cost; return true; }
    int spendAll() { int x = current; current = 0; return x; } // X コスト
}

enum Phase { BEGIN, PLAYER, END_PLAYER, ENEMY, FINISHED }
```

## インターフェース

```java
class TurnController {
    void update(float delta);
    void requestEndTurn();          // input から
    Phase phase();
    boolean isPlayerInputAllowed(); // PLAYER かつ actionManager.isIdle()
}
```

## 依存関係

| ライブラリ / サービス | 用途 |
|-----------------------|------|
| action-queue | 全定常処理の実行・isIdle 判定 |
| powers | atTurnStart/atTurnEnd フックの効果 |
| enemy | takeTurn・次インテント決定 |
| cards | ドロー/手札処理（pile 操作） |
| effects | フェーズ演出（任意） |
| run（RunRng） | シャッフルの決定的乱数 |

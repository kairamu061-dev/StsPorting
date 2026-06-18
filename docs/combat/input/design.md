# input 設計

## 技術選定

| 技術 | 用途 | 選定理由 |
|------|------|----------|
| foundation InputConsumer（仮想座標） | 入力受付 | 解像度非依存のヒットテスト |
| 入力状態機械（enum） | 操作管理 | IDLE/HOVER/DRAGGING/TARGETING を明示し誤爆防止 |
| 補間（lerp）でカード追従/整列 | 手触り | 原作のなめらかな持ち上げ・整列を再現（effects と分担） |

## アーキテクチャ

```
com.stsporting.combat.input
├── CombatInputController … InputConsumer 実装。状態機械の中心
├── InputState (enum)     … IDLE, HOVER, DRAGGING, TARGETING
├── HandLayout            … 弧状レイアウト（各カードの位置/角度/当たり矩形）
├── HitTester             … カード/敵/ボタン/ポーションのヒットテスト
└── PlayIntentResolver    … リリース時の確定/キャンセル判定 → cards へ
```

- `CombatInputController` は `TurnController.isPlayerInputAllowed()` が true のときのみ入力を処理。
- `HandLayout` が手札の各カードの中心座標・回転角・矩形を算出（ホバー時は対象カードを拡大・前面 z）。
- リリース時に `PlayIntentResolver` が対象種別と位置から play/cancel を決め、play なら `actionManager.queueCard(new CardQueueItem(card, target))`。

## 弧状レイアウト計算

```java
class HandLayout {
    // n 枚を中心角 spread 度の弧に配置
    Pose poseFor(int index, int n) {
        float t = (n == 1) ? 0.5f : index / (float)(n - 1);   // 0..1
        float angle = lerp(+spread/2, -spread/2, t);          // 左→右で回転
        float x = centerX + (t - 0.5f) * handWidth(n);
        float y = baseY + arcLift(t);                          // 弧の盛り上がり
        return new Pose(x, y, angle);
    }
    Rectangle hitRect(int index, int n);   // 当たり判定（ホバー時は拡大矩形）
}
```

## 状態遷移ロジック

```java
void onMouseMoved(float vx, float vy) {
    if (!allowed()) return;
    if (state == IDLE || state == HOVER) {
        hovered = hitTester.cardAt(vx, vy);
        state = (hovered != null) ? HOVER : IDLE;
    }
}
void onTouchDown(float vx, float vy, int button) {
    if (!allowed()) { tryButtons(vx, vy); return; } // ターン終了/ポーションは別途
    if (state == HOVER && hovered != null) {
        dragCard = hovered;
        state = (dragCard.target == ENEMY) ? TARGETING : DRAGGING;
    } else tryButtons(vx, vy);
}
void onTouchUp(float vx, float vy, int button) {
    if (state == DRAGGING) {            // 対象なしカード
        if (vy > PLAY_THRESHOLD_Y && playable(dragCard)) play(dragCard, null);
        else cancel(dragCard);
    } else if (state == TARGETING) {    // 単体対象
        Enemy e = hitTester.enemyAt(vx, vy);
        if (e != null && playable(dragCard)) play(dragCard, e);
        else cancel(dragCard);
    }
    dragCard = null; state = IDLE;
}
```

- `PLAY_THRESHOLD_Y`：手札より十分上のライン。原作の「上に放ると発動」を再現し、誤爆を防ぐ。
- `play()` は `actionManager.queueCard(...)`、`cancel()` は手札整列へ戻す（＋プレイ不可なら effects に赤フィードバック合図）。

## インターフェース

```java
class CombatInputController implements InputConsumer {
    boolean onTouchDown/onTouchUp/onMouseMoved/onKeyDown(...);
    AbstractCard hoveredCard();      // effects が拡大描画
    AbstractCard draggingCard();     // effects が追従/矢印描画
    InputState state();
}
```

## 依存関係

| ライブラリ / サービス | 用途 |
|-----------------------|------|
| foundation（InputConsumer/Viewport） | 仮想座標入力 |
| turn-flow | 入力可否（isPlayerInputAllowed）・requestEndTurn |
| cards / action-queue | プレイ要求（queueCard）・プレイ可否 |
| effects | 拡大/矢印/赤フィードバックの描画（座標・状態はここ） |
| run（ポーション） | ポーション使用要求 |

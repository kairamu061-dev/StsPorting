# event（イベント実行基盤）設計

## 技術選定

| 技術 | 用途 | 選定理由 |
|------|------|----------|
| libGDX Screen | イベント画面 | foundation 準拠 |
| RunRng（event 系統） | イベント抽選 | 決定的 |
| content/events（AbstractEvent） | 内容 | 仕組みと内容の分担 |

## アーキテクチャ

```
com.stsporting.run.event
├── EventScreen         … 本文/選択肢描画・入力・分岐
├── EventRunner         … apply 実行→outcome→次状態（STAY/LEAVE/NEXT/COMBAT）
└── (CardGridView 再利用) … カード入手/削除選択
```

## 主要ロジック

```java
class EventRunner {
    AbstractEvent event;
    void choose(EventOption opt) {
        if (!opt.enabled(rs)) return;
        EventOutcome outcome = opt.apply(rs);
        switch (outcome.kind) {
            case LEAVE: finish(); break;                 // NodeResult(COMPLETED)
            case STAY: case NEXT: render(event.options(rs)); break;
            case START_COMBAT:
                controller.startCombat(outcome.encounter, result -> {
                    if (result.outcome == VICTORY) { /* 残り報酬適用 */ finish(); }
                    // DEFEAT は combat 側でゲームオーバー
                });
                break;
        }
    }
}
```

## インターフェース

```java
EventScreen.show(runState, runRng, runController,
                 () -> controller.onNodeResult(COMPLETED));
// 抽選: EventId id = EventLibrary.roll(runRng); AbstractEvent e = EventLibrary.newEvent(id);
```

## 依存関係

| ライブラリ / サービス | 用途 |
|-----------------------|------|
| foundation | 画面・入力・描画 |
| run（RunState/RunController/RunRng） | 状態・戦闘起動・完了通知・抽選 |
| content/events | イベント内容（本文/選択肢/結果） |
| combat | 戦闘誘発（START_COMBAT） |
| combat/cards（CardGridView） | カード入手/削除の選択 |

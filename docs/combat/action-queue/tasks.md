# action-queue タスク

## 実装タスク一覧

<!-- ステータス: [ ] 未着手 / [~] 進行中 / [x] 完了 -->

- [x] `GameAction` 抽象基底（update/isDone/duration/tickDuration）
- [x] `ActionManager`：queue(Deque)・current・update ループ・isIdle
- [x] `addToTop` / `addToBottom` の順序規約実装（割り込みは LIFO）
- [ ] `cardQueue` と `queueCard`（プレイ要求の投入と展開）← cards サブ項目で実装
- [x] `WaitAction`（純演出待ち）
- [x] `DamageAction`（攻撃修正→被ダメ修正→ブロック→HP→被弾フック割り込み）
- [x] `GainBlockAction`（修正適用）
- [x] `LoseHpAction`（ブロック無視の直接 HP 減）
- [ ] `DrawAction`（山札切れシャッフル割り込み）← cards サブ項目で実装
- [ ] `DiscardAction` / `ExhaustAction` ← cards サブ項目で実装
- [x] `ApplyPowerAction` / `ReducePowerAction`
- [x] ヘッドレスユニットテスト（解決順・割り込み LIFO・duration・ダメージ計算順・とげ反撃）— 計15件 PASS

## 依存関係

- combat 親の CombatState/Creature → 全アクションの更新対象
- powers の修正値/被弾フック → DamageAction/ApplyPowerAction
- effects の合図/duration → 演出待ち
- action-queue 完成 → turn-flow / cards / powers / enemy の前提（最優先）

## ステータス

In progress（キュー機構・ダメージ計算・基本アクション・コアパワー実装＋テスト完了。
カード pile 系アクション（Draw/Discard/Exhaust）と cardQueue は cards サブ項目で実装予定）

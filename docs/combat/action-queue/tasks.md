# action-queue タスク

## 実装タスク一覧

<!-- ステータス: [ ] 未着手 / [~] 進行中 / [x] 完了 -->

- [ ] `GameAction` 抽象基底（update/isDone/duration/tickDuration）
- [ ] `ActionManager`：queue(Deque)・current・update ループ
- [ ] `addToTop` / `addToBottom` の順序規約実装
- [ ] `cardQueue` と `queueCard`（プレイ要求の投入と展開）
- [ ] `WaitAction`（純演出待ち）
- [ ] `DamageAction`（攻撃修正→被ダメ修正→ブロック→HP→被弾フック割り込み）
- [ ] `GainBlockAction`（修正適用）
- [ ] `DrawAction`（山札切れ時の捨て札シャッフル割り込み）
- [ ] `DiscardAction` / `ExhaustAction`
- [ ] `ApplyPowerAction` / `ReducePowerAction`
- [ ] ヘッドレスユニットテスト（解決順・割り込み・ドロー・ダメージ計算・連鎖収束）

## 依存関係

- combat 親の CombatState/Creature → 全アクションの更新対象
- powers の修正値/被弾フック → DamageAction/ApplyPowerAction
- effects の合図/duration → 演出待ち
- action-queue 完成 → turn-flow / cards / powers / enemy の前提（最優先）

## ステータス

Todo（ドキュメント整備完了。combat 内で最優先実装）

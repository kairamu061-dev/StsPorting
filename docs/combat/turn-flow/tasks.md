# turn-flow タスク

## 実装タスク一覧

<!-- ステータス: [ ] 未着手 / [~] 進行中 / [x] 完了 -->

- [ ] `Phase` enum と `TurnController` 骨組み（isIdle 連動の遷移）
- [ ] `EnergyManager`（最大/回復/消費/X コスト）
- [ ] 戦闘開始処理（シャッフル・開始フック・初期ドロー）
- [ ] プレイヤーターン開始（ブロックリセット・atTurnStart・エネ回復・ドロー）
- [ ] ターン終了処理（atTurnEnd・手札処理）と requestEndTurn
- [ ] 敵ターン処理（各敵 atTurnStart→takeTurn→atTurnEnd・次インテント）
- [ ] 各遷移点の勝敗チェックと FINISHED
- [ ] ヘッドレステスト（1 ターン進行・フック順・エネ回復・勝敗遷移）

## 依存関係

- action-queue 完成 → turn-flow の前提
- powers（フック）・enemy（takeTurn）・cards（pile）の最小実装 → 通し動作
- turn-flow → 戦闘の縦通し（CombatScreen 結合）

## ステータス

Todo（ドキュメント整備完了。action-queue の次に着手）

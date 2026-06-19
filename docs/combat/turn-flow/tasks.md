# turn-flow タスク

## 実装タスク一覧

<!-- ステータス: [ ] 未着手 / [~] 進行中 / [x] 完了 -->

- [x] `Phase` enum と `TurnController`（isIdle 連動の遷移）
- [x] `EnergyManager`（最大/回復/消費/spendAll）
- [x] 戦闘開始処理（シャッフル・初期ドロー）※レリック開始フックは relics 接続時
- [x] プレイヤーターン開始（ブロックリセット・atStartOfTurn・エネ回復・ドロー）
- [x] ターン終了処理（atEndOfTurn・弱体/脆弱の減衰・手札捨て）と requestEndTurn
- [x] 敵ターン処理（各敵 block リセット→atStartOfTurn→takeTurn→atEndOfTurn→減衰→次インテント）
- [x] 勝敗チェック（idle 時に判定）と FINISHED
- [x] ヘッドレステスト（開幕ドロー/エネ・ターン終了→敵攻撃・ブロックリセット・弱体減衰・勝敗）— 6件 PASS
- [ ] X コスト・onUseCard 発火・手札保持(Retain) ← cards/powers/relics 接続時

## 依存関係

- action-queue 完成 → turn-flow の前提
- powers（フック）・enemy（takeTurn）・cards（pile）の最小実装 → 通し動作
- turn-flow → 戦闘の縦通し（CombatScreen 結合）

## ステータス

In progress（フェーズ機械・エネルギー・ターン境界フック・勝敗判定 実装＋テスト完了。
レリック開始フック/X コスト/Retain は各接続時に対応）

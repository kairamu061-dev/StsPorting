# enemy タスク

## 実装タスク一覧

<!-- ステータス: [ ] 未着手 / [~] 進行中 / [x] 完了 -->

- [x] `IntentType` enum・`Intent` 表示モデル
- [x] `EnemyMove`（id/種別/baseDamage/hits/weight/maxConsecutive/enqueue）＋ `Moves` ファクトリ・`MoveEffect`
- [x] `AbstractMonster`（Creature 継承・nextMove・moveHistory・takeTurn・initialize）
- [x] `MoveSelector`（連続制限除外＋重み抽選・決定的・フォールバック）
- [x] 予告ダメージ計算（`getIntent` が PowerHooks を共有）
- [x] `rollNextMove` の既定実装（MoveSelector）。固定/状態遷移敵はオーバーライド
- [x] 初期化（HP ロール・初期インテント）
- [x] ヘッドレステスト（決定的選択・連続制限・予告=実ダメージ一致・takeTurn）— 5件 PASS

## 依存関係

- combat 親 Creature・action-queue・powers → enemy の前提
- turn-flow → 敵ターンでの takeTurn 呼び出し
- content/monsters → 各敵の moves で通し検証

## ステータス

In progress（敵モデル・インテント・行動AI・takeTurn 実装＋テスト完了。
具体敵は content/monsters、敵ターン駆動は turn-flow で接続）

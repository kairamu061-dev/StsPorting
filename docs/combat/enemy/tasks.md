# enemy タスク

## 実装タスク一覧

<!-- ステータス: [ ] 未着手 / [~] 進行中 / [x] 完了 -->

- [ ] `IntentType` enum・`Intent` 表示モデル
- [ ] `EnemyMove`（id/種別/baseDamage/hits/weight/maxConsecutive/enqueue）
- [ ] `AbstractMonster`（Creature 継承・nextMove・moveHistory・takeTurn）
- [ ] `MoveSelector`（連続制限除外＋重み抽選・決定的）
- [ ] 予告ダメージ計算（DamageAction と同一修正関数を共有）
- [ ] `rollNextMove` の既定実装とフォールバック
- [ ] 初期化（HP ロール・初期インテント）
- [ ] ヘッドレステスト（決定的選択・連続制限・予告=実ダメージ一致）

## 依存関係

- combat 親 Creature・action-queue・powers → enemy の前提
- turn-flow → 敵ターンでの takeTurn 呼び出し
- content/monsters → 各敵の moves で通し検証

## ステータス

Todo（ドキュメント整備完了）

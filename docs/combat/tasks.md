# combat タスク

## 実装タスク一覧

<!-- ステータス: [ ] 未着手 / [~] 進行中 / [x] 完了 -->

親（横断責務）:

- [ ] `CombatRequest` / `CombatResult` 境界の定義（run との契約）
- [ ] `CombatState`（プレイヤー/敵/各 pile/エネルギー/ターン/フェーズ）
- [ ] `Creature` 基底（HP/ブロック/パワー）と `Player`
- [ ] ダメージ/ブロック計算の共通ユーティリティ（攻撃修正→被ダメ修正→ブロック→HP）
- [ ] `CombatScreen` の update/描画ループとアクションキュー駆動
- [ ] 勝敗判定と CombatResult 返却（run へ）
- [ ] 戦闘全体のヘッドレス結合テスト（1ターン進行の決定的検証）

サブ項目（各 tasks.md を参照）:

- [ ] [combat/action-queue](./action-queue/tasks.md) … 解決順序の根幹（最優先）
- [ ] [combat/turn-flow](./turn-flow/tasks.md)
- [ ] [combat/cards](./cards/tasks.md)
- [ ] [combat/powers](./powers/tasks.md)
- [ ] [combat/enemy](./enemy/tasks.md)
- [ ] [combat/input](./input/tasks.md)
- [ ] [combat/effects](./effects/tasks.md)

## 依存関係

- foundation 完了 → combat 全タスクの前提
- action-queue → turn-flow / cards / powers / enemy（全システムがキュー経由で解決）
- CombatState / Creature → 各サブ項目の土台
- cards・powers・enemy の最小実装 → 戦闘結合テスト
- input・effects → ロジック確定後に体感（操作性/演出）を上載せ
- content（最小カード/敵セット）→ 戦闘の実通し

## ステータス

In progress（縦の動線が成立：action-queue / powers / cards / enemy / turn-flow を実装し、
content（初期デッキ＋Cultist）と `CombatScreen` を配線。実コンテンツで1戦闘を勝利まで通す
ヘッドレス統合テストが PASS（全49テスト緑）。input/effects は簡易版で、本実装（弧状手札・
ドラッグ・本格演出）と GUI 実機確認が次段）

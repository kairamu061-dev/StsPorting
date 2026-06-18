# potions（content）タスク

## 実装タスク一覧

<!-- ステータス: [ ] 未着手 / [~] 進行中 / [x] 完了 -->

- [ ] `PotionId` enum・`AbstractPotion`（needsTarget/combatOnly/use）
- [ ] `PotionLibrary`（登録/生成）
- [ ] 自己系：回復 / ブロック / 筋力 / エナジー / ドロー
- [ ] 敵対象系：弱体 / 脆弱 / 炎（ダメージ）
- [ ] run のポーション使用フロー連携（対象選択・スロット消費）
- [ ] ユニットテスト（各効果・対象要否・combatOnly 制約）

## 依存関係

- combat（action-queue/powers/EnergyManager）・run（スロット）・input（対象選択）確定 → 使用処理の前提
- content（ContentRegistry）→ 登録

## ステータス

Todo（ドキュメント整備完了）

# events（content）タスク

## 実装タスク一覧

<!-- ステータス: [ ] 未着手 / [~] 進行中 / [x] 完了 -->

- [ ] `EventId` enum・`AbstractEvent`/`EventOption`/`EventOutcome`
- [ ] `EventLibrary`（登録/生成）
- [ ] リスク報酬型（Big Fish）
- [ ] 取引型イベント
- [ ] リスク型（Cursed Tome：段階的 HP 支払い）
- [ ] 報酬+代償型（Golden Idol）
- [ ] 戦闘誘発型（START_COMBAT 連携）
- [ ] ユニットテスト（各 apply の RunState 変化・条件 enabled・戦闘戻り適用）

## 依存関係

- run/event（実行基盤）確定 → 提示・分岐の前提
- content（cards/relics/potions）・combat（戦闘誘発）→ 結果の参照先
- content（ContentRegistry）→ 登録

## ステータス

Todo（ドキュメント整備完了。content 最後のサブ項目）

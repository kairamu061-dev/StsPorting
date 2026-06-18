# relics（content）タスク

## 実装タスク一覧

<!-- ステータス: [ ] 未着手 / [~] 進行中 / [x] 完了 -->

- [ ] `RelicId` enum・`AbstractRelic`（全フック既定 no-op）
- [ ] `RelicManager`（保持集合・各タイミングのフック一括発火）
- [ ] `RelicLibrary`（登録/生成）
- [ ] Burning Blood（onVictory HP+6）
- [ ] Blood Vial / Anchor / Bronze Scales / Vajra（atBattleStart 系）
- [ ] Pen Nib 系（onUseCard カウント）
- [ ] 取得時HP系（onEquip）
- [ ] combat（atBattleStart/atTurnStart/onUseCard/onVictory）・run（onEquip）への接続
- [ ] ユニットテスト（各フック発火・カウントリセット・HP クランプ）

## 依存関係

- combat（フック点）・run（取得処理）確定 → レリック接続の前提
- content（ContentRegistry）→ 登録

## ステータス

Todo（ドキュメント整備完了）

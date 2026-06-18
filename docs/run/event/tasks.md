# event（イベント実行基盤）タスク

## 実装タスク一覧

<!-- ステータス: [ ] 未着手 / [~] 進行中 / [x] 完了 -->

- [ ] イベント抽選（EventLibrary.roll・決定的）
- [ ] `EventScreen`（本文/選択肢描画・条件非活性）
- [ ] `EventRunner`（apply→outcome→分岐）
- [ ] 結果分岐（LEAVE/STAY/NEXT/START_COMBAT）
- [ ] 戦闘往復（startCombat→CombatResult→続き）
- [ ] カード入手/削除の CardGridView 連携
- [ ] 「立ち去る」既定肢の保証（詰み防止）
- [ ] ユニットテスト（分岐・条件非活性・戦闘戻り・決定的抽選）

## 依存関係

- foundation・run（RunState/RunController/RunRng）・content/events → 前提
- combat（戦闘誘発）・CardGridView（選択）→ 連携

## ステータス

Todo（ドキュメント整備完了。run サブ項目の最後）

# powers タスク

## 実装タスク一覧

<!-- ステータス: [ ] 未着手 / [~] 進行中 / [x] 完了 -->

- [x] `PowerType` enum・`AbstractPower` 基底（全フック既定 no-op・stack/shouldRemove/reducePerTurn）
- [x] `PowerHooks`（atDamageGive/Receive・modifyBlock の横断呼び出し）
- [x] `ApplyPowerAction` / `ReducePowerAction`
- [x] 筋力 Strength
- [x] 弱体 Weak（×0.75・ターン減衰）
- [x] 脆弱 Vulnerable（×1.5・ターン減衰）
- [x] 中毒 Poison（ターン頭 自傷＋減衰）
- [x] 金属化 Metallicize（ターン終わり ブロック）
- [x] とげ Thorns（被弾時 反撃 addToTop）
- [x] 再生 Regen（ターン頭 回復＋減衰）
- [x] ヘッドレステスト（修正順・スタック加算・0消滅・減衰・とげ反撃）

> 注: action-queue との相互依存のため、本サブ項目の実装は action-queue と同時に完了（combat/action-queue/dev-notes 参照）。turn-flow（atTurnStart/atTurnEnd フックの発火元）接続は turn-flow 実装時に行う。

## 依存関係

- action-queue（DamageAction の修正呼び出し点）→ powers の前提
- turn-flow（atTurnStart/atTurnEnd）→ 中毒/金属化/減衰の発火
- powers → cards/enemy/content の効果表現で利用

## ステータス

In progress（コアパワー＋フック基盤実装・テスト完了。turn-flow からのフック発火接続が残）

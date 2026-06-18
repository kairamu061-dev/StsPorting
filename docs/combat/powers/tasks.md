# powers タスク

## 実装タスク一覧

<!-- ステータス: [ ] 未着手 / [~] 進行中 / [x] 完了 -->

- [ ] `PowerType` enum・`AbstractPower` 基底（全フック既定 no-op・stack/shouldRemove/reducePerTurn）
- [ ] `PowerHooks`（atDamageGive/Receive 等の横断呼び出し）
- [ ] `ApplyPowerAction` / `ReducePowerAction`
- [ ] 筋力 Strength
- [ ] 弱体 Weak（×0.75・ターン減衰）
- [ ] 脆弱 Vulnerable（×1.5・ターン減衰）
- [ ] 中毒 Poison（ターン頭 自傷＋減衰）
- [ ] 金属化 Metallicize（ターン終わり ブロック）
- [ ] とげ Thorns（被弾時 反撃 addToTop）
- [ ] 再生 Regen（ターン頭 回復＋減衰）
- [ ] ヘッドレステスト（修正順・スタック加算・0消滅・減衰・とげ反撃）

## 依存関係

- action-queue（DamageAction の修正呼び出し点）→ powers の前提
- turn-flow（atTurnStart/atTurnEnd）→ 中毒/金属化/減衰の発火
- powers → cards/enemy/content の効果表現で利用

## ステータス

Todo（ドキュメント整備完了）

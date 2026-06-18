# cards タスク

## 実装タスク一覧

<!-- ステータス: [ ] 未着手 / [~] 進行中 / [x] 完了 -->

- [x] `CardType` / `CardTarget` enum
- [x] `AbstractCard`（種別/コスト/対象/可変状態/use/upgrade/isPlayable/cost）
- [x] 4 pile（CombatState に drawPile/hand/discardPile/exhaustPile を保持）
- [x] `CardDrawHelper`（draw・山札切れシャッフル・手札上限10）
- [x] `DrawAction` / `DiscardAction` / `ExhaustAction`（action-queue から委譲分も実装）
- [x] `PlayCardFlow.resolve`（可否→コスト消費→use→PostPlayAction）
- [x] `PostPlayAction`（POWER消費/exhaust/discard の使用後移動）
- [x] コスト変動（costForTurn/freeToPlayOnce）。X コストは turn-flow(EnergyManager) 実装時に
- [x] ヘッドレステスト（draw/shuffle/上限/プレイ可否/使用後移動）— 9件 PASS
- [ ] カードプレイ時フック（onUseCard）の発火 ← powers/relics 接続時に turn-flow と統合

## 依存関係

- action-queue・turn-flow(EnergyManager) → cards のプレイ処理前提
- powers → カードプレイ時フック・修正
- content/cards → 具体カードで通し検証

## ステータス

In progress（カードモデル・pile・draw/play・使用後移動を実装＋テスト完了。
onUseCard フック発火と X コストは turn-flow 接続時に対応）

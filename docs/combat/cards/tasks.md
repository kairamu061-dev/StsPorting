# cards タスク

## 実装タスク一覧

<!-- ステータス: [ ] 未着手 / [~] 進行中 / [x] 完了 -->

- [ ] `CardType` / `CardTarget` enum
- [ ] `AbstractCard`（種別/コスト/対象/可変状態/use/canUse/upgrade/isPlayable）
- [ ] `CardPiles`（4 pile の保持と移動 API・onExhaust フック）
- [ ] `CardDrawHelper`（draw・山札切れシャッフル・手札上限10）
- [ ] `PlayCardFlow.resolve`（可否→コスト消費→use→PostPlayAction）
- [ ] `PostPlayAction`（POWER消費/exhaust/discard の使用後移動）
- [ ] コスト変動（一時0/freeToPlayOnce/X コスト）
- [ ] ヘッドレステスト（draw/shuffle/上限/プレイ可否/使用後移動）

## 依存関係

- action-queue・turn-flow(EnergyManager) → cards のプレイ処理前提
- powers → カードプレイ時フック・修正
- content/cards → 具体カードで通し検証

## ステータス

Todo（ドキュメント整備完了）

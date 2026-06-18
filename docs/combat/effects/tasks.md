# effects タスク

## 実装タスク一覧

<!-- ステータス: [ ] 未着手 / [~] 進行中 / [x] 完了 -->

- [ ] `AbstractEffect`・`EffectManager`（更新/描画/破棄/プール）
- [ ] `DamageNumberEffect` / `BlockNumberEffect` / `HealNumberEffect`
- [ ] `HitFlashEffect` / `ShakeCreatureEffect` / `SlashParticleEffect`
- [ ] `GainBlockSparkEffect`
- [ ] `ScreenShake`（カメラオフセット・減衰）
- [ ] `CardMoveEffect`（from→to 補間：プレイ/ドロー/捨て/消滅）
- [ ] `PowerIconRenderer` / `IntentRenderer`（論理値の可視化）
- [ ] `HandRenderer`（input 連動：拡大/追従/矢印）
- [ ] `CombatVfx` API と action-queue の duration 連携
- [ ] レンダリング z 順の統合・実機でのテンポ/打撃感確認

## 依存関係

- foundation（描画）・action-queue（発火/duration）→ effects の前提
- powers/enemy/cards/input の論理値 → 描画対象
- 打撃感/テンポの最終確認は Windows 実機

## ステータス

Todo（ドキュメント整備完了。ロジック確定後、実機で演出を反復調整）

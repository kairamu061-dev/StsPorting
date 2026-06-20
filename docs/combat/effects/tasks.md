# effects タスク

## 実装タスク一覧

<!-- ステータス: [ ] 未着手 / [~] 進行中 / [x] 完了 -->

- [x] `AbstractEffect`・`EffectManager`（更新/プルーン/上限・描画）
- [x] `DamageNumberEffect`（ダメージ赤/ブロック青/HP減 紫を色分けで兼用：上昇＋フェード）
- [x] `ScreenShake`（カメラオフセット・線形減衰）
- [x] `CombatListener` フック（onDamageDealt/onBlockGained/onHpLost）でロジック→演出を疎結合接続
- [x] レンダリング統合（CombatScreen：シェイクでカメラオフセット、数値は最前面）
- [x] ヘッドレステスト（エフェクト寿命/プルーン・数値の上昇/フェード・シェイク減衰）— 4件 PASS
- [ ] `HitFlashEffect` / `SlashParticleEffect`（被弾フラッシュ・斬撃）
- [ ] `CardMoveEffect`（プレイ/ドロー/捨て/消滅の飛翔）
- [x] パワーアイコン（バフ/デバフを色分けチップ＋短縮名＋スタック数で表示）／インテントの色チップ＋ダメージ値
- [x] カード飛翔アニメ（プレイ/捨て札時に縮小しつつ捨て札方向へ）
- [ ] パワー/インテントの本アイコン画像化（現状は色チップ＋短縮名）
- [ ] action-queue の duration とのテンポ連携の精緻化・実機での打撃感調整

## 依存関係

- foundation（描画）・action-queue（発火/duration）→ effects の前提
- powers/enemy/cards/input の論理値 → 描画対象
- 打撃感/テンポの最終確認は Windows 実機

## ステータス

In progress（CombatListener 方式でロジックと疎結合に演出を接続。数値ポップ（ダメージ/ブロック/HP減）と
画面シェイクを実装・テスト・CombatScreen 統合済み。被弾フラッシュ/斬撃/カード移動アニメ/アイコン化と
テンポ精緻化が次段）

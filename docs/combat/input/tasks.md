# input タスク

## 実装タスク一覧

<!-- ステータス: [ ] 未着手 / [~] 進行中 / [x] 完了 -->

- [ ] `InputState` enum・`CombatInputController`（InputConsumer 実装）
- [ ] `HandLayout`（弧状配置・位置/角度/当たり矩形・ホバー拡大）
- [ ] `HitTester`（カード/敵/ボタン/ポーション）
- [ ] ホバー処理（最前面カード選択・拡大前面化）
- [ ] ドラッグ（DRAGGING/TARGETING 分岐・カーソル追従）
- [ ] リリース確定（対象なし=上方向閾値、単体=敵ドロップ、cancel）
- [ ] `PlayIntentResolver`（queueCard 連携・プレイ不可フィードバック合図）
- [ ] ポーション使用・ターン終了（requestEndTurn）
- [ ] 入力可否制御（解決中は無効）
- [ ] テスト（ヒットテスト・状態遷移・閾値判定）＋実機での手触り確認

## 依存関係

- foundation(InputConsumer)・turn-flow・cards/action-queue → input の前提
- effects → 拡大/矢印/フィードバック描画
- 手触りの最終確認は Windows 実機

## ステータス

Todo（ドキュメント整備完了。ロジック確定後、実機で操作感を反復調整）

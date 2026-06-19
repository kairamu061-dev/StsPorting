# input タスク

## 実装タスク一覧

<!-- ステータス: [ ] 未着手 / [~] 進行中 / [x] 完了 -->

- [x] `InputState` enum・`CombatInputController`（仮想座標イベントを受ける）
- [x] `HandLayout`（弧状配置・位置/角度/当たり矩形・ホバー拡大用 Pose）
- [x] ヒットテスト（カード：最前面優先／敵：TargetResolver）
- [x] ホバー処理（カード選択・拡大前面化は CombatScreen 描画）
- [x] ドラッグ（DRAGGING/TARGETING 分岐・カーソル追従＝touchDragged→onMouseMoved）
- [x] リリース確定（対象なし=上方向閾値、単体=敵ドロップ、cancel）
- [x] プレイ不可フィードバック合図（lastPlayRejected）＋ queueCard 経由不要化（PlayCardFlow 直結）
- [x] ターン終了（requestEndTurn は CombatScreen のボタン）
- [x] 入力可否制御（isPlayerInputAllowed 連動）
- [x] テスト（弧状配置・状態遷移・上方向閾値・敵ドロップ・エネ不足・hover）— 11件 PASS
- [ ] ポーション使用 UI（run のポーション管理実装時）
- [ ] カード回転描画・整列補間など見た目の作り込み＋実機での手触り調整

## 依存関係

- foundation(InputConsumer)・turn-flow・cards/action-queue → input の前提
- effects → 拡大/矢印/フィードバック描画
- 手触りの最終確認は Windows 実機

## ステータス

In progress（状態機械・弧状手札・ドラッグ＆ドロップ・上方向閾値/敵ドロップ確定・対象矢印・ホバー拡大を実装。
ロジックは 11 件のヘッドレステストで担保。CombatScreen に統合済み。
カード回転描画/整列補間の作り込みとポーション UI、実機での手触り調整が次段）

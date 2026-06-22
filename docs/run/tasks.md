# run タスク

## 実装タスク一覧

<!-- ステータス: [ ] 未着手 / [~] 進行中 / [x] 完了 -->

親（横断責務）:

- [ ] `RunState` データモデル＋更新メソッド（heal/loseHp/gold/card/potion/relic・クランプ）
- [ ] `RunRng` 系統別 RNG（シード派生・決定的）
- [ ] `RunController` ノード解決ループ（NodeType → 子画面 → NodeResult 反映）
- [ ] アイアンクラッド初期 RunState 生成（初期 HP/デッキ/レリック/金）
- [ ] `TopBar` HUD（HP/金/レリック/ポーション/フロア・ポーション使用）
- [ ] `RewardBuilder` 報酬抽選（金/カード3択/ポーション/レリック）
- [ ] `RewardScreen` 報酬 UI
- [ ] `GameOverScreen` / `VictoryScreen`
- [ ] RunState・RunRng・RewardBuilder のヘッドレスユニットテスト

サブ項目（各ディレクトリの tasks.md 参照）:

- [ ] [run/rest-site](./rest-site/tasks.md)
- [ ] [run/shop](./shop/tasks.md)
- [ ] [run/treasure](./treasure/tasks.md)
- [ ] [run/event](./event/tasks.md)

## 依存関係

- foundation 完了 → run 全タスクの前提
- RunState → RunController / TopBar / 報酬（状態の更新先）
- RunController → 各ノード子画面の起動
- combat / map / content の最小実装 → エンドツーエンドのノード解決
- 親の RunController/RunState → 各サブ項目（子画面は NodeResult を返す契約に従う）

## ステータス

In progress（RunState/RunRng/RunController を実装し、map→戦闘→報酬→map のラン動線を結線。
CombatScreen を CombatRequest/CombatResult 駆動に改修、MapScreen/RewardScreen/GameOverScreen を追加。
ボス勝利でクリア・敗北でゲームオーバー。ロジックは fake navigator で 7 件ヘッドレステスト済み。
休憩=回復のみ簡易、ショップ/宝箱/イベント/ポーション/レリックはサブ項目で順次実装）

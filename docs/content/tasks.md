# content タスク

## 実装タスク一覧

<!-- ステータス: [ ] 未着手 / [~] 進行中 / [x] 完了 -->

親（横断責務）:

- [ ] ID 体系（CardId/RelicId/PotionId/MonsterId/EventId）
- [ ] `ContentRegistry`（登録/取得/エンカウンター抽選）
- [ ] 各 *Library の登録フック（起動時初期化）
- [ ] レジストリのユニットテスト（未登録 ID 検知・取得）

サブ項目（各 tasks.md を参照）:

- [ ] [content/cards](./cards/tasks.md) … 初期デッキ＋コア報酬カード
- [ ] [content/relics](./relics/tasks.md) … 初期＋代表フックレリック
- [ ] [content/potions](./potions/tasks.md)
- [ ] [content/monsters](./monsters/tasks.md) … 第1幕 通常/エリート/ボス＋編成
- [ ] [content/events](./events/tasks.md)

## 依存関係

- combat の GameAction/AbstractPower/Creature 確定 → カード/敵/レリック効果の実装前提
- ContentRegistry → run/map/combat からの参照
- content/cards・content/monsters の最小セット → 戦闘の実通し

## ステータス

Todo（親ドキュメント整備完了・5 サブ項目へ分割済み。詳細は順次整備）

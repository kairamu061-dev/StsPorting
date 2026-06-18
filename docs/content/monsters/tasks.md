# monsters（content）タスク

## 実装タスク一覧

<!-- ステータス: [ ] 未着手 / [~] 進行中 / [x] 完了 -->

- [ ] `MonsterId` enum・`MonsterLibrary`（登録/生成）
- [ ] 行動ヘルパ `Moves`（attack/defend/buff/debuff の EnemyMove 生成）
- [ ] 通常敵：Cultist（儀式→攻撃）
- [ ] 通常敵：JawWorm（攻撃/防御/バフ混合）
- [ ] 通常敵：小型複数編成（解決順の代表）
- [ ] エリート：GremlinNob（Enrage = onUseCard 反応）
- [ ] エリート：Lagavulin（睡眠→覚醒の状態遷移）
- [ ] ボス：TheGuardian（モードシフト）
- [ ] `EncounterTable`（act1 weak/strong/elite/boss・rollEncounter）
- [ ] ユニットテスト（決定的行動・予告=実ダメージ・モードシフト・Enrage）

## 依存関係

- combat/enemy・powers・action-queue 確定 → 各敵実装の前提
- content/cards（最小デッキ）→ 実戦闘の通し
- map/run → エンカウンター割り当て・戦闘起動

## ステータス

Todo（ドキュメント整備完了。content/cards と合わせ「1戦闘通し」の最小セット）

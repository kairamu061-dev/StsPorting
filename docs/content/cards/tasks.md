# cards（content）タスク

## 実装タスク一覧

<!-- ステータス: [ ] 未着手 / [~] 進行中 / [x] 完了 -->

- [ ] `CardId` enum（初期＋報酬カード分）
- [ ] `CardLibrary`（登録・newCard）
- [ ] 初期デッキ：Strike / Defend / Bash（＋アップグレード）
- [ ] 報酬コモン：Anger / Cleave / IronWave / BodySlam / TwinStrike / PommelStrike
- [ ] パワー/スキル：Flex（一時筋力）/ Inflame / Metallicize
- [ ] `CardPools`（レアリティ別プール・rollReward）
- [ ] アップグレード差分の実装
- [ ] ユニットテスト（各 use() の効果・+差分・Body Slam のブロック依存・Flex のターン戻し）

## 依存関係

- combat/cards・action-queue・powers 確定 → 各カード実装の前提
- content（ContentRegistry）→ 登録
- run（RewardBuilder）→ 報酬抽選で利用

## ステータス

Todo（ドキュメント整備完了。content の最優先＝戦闘を動かす最小セット）

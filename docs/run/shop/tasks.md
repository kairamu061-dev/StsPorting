# shop（ショップ）タスク

## 実装タスク一覧

<!-- ステータス: [ ] 未着手 / [~] 進行中 / [x] 完了 -->

- [ ] `ShopStock`（在庫＋価格の決定的生成）
- [ ] `ShopScreen`（陳列・退出）
- [ ] 購入（カード/レリック/ポーション：spendGold→add→在庫除去）
- [ ] カード削除サービス（価格逓増・来店制限）
- [ ] `CardGridView`（rest-site と共通化）
- [ ] 所持金不足/上限の非活性表示
- [ ] ユニットテスト（購入で gold/所持物更新・削除コスト逓増・決定的在庫）

## 依存関係

- foundation・run（RunState/RunRng）・content（プール）→ 前提
- CardGridView は rest-site と共通

## ステータス

Todo（ドキュメント整備完了）

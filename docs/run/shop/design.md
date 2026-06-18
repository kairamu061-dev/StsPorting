# shop（ショップ）設計

## 技術選定

| 技術 | 用途 | 選定理由 |
|------|------|----------|
| libGDX Screen | 店舗 UI | foundation 準拠 |
| RunRng（shop 系統） | 在庫生成 | 決定的な品揃え |

## アーキテクチャ

```
com.stsporting.run.shop
├── ShopScreen          … 陳列・購入・削除・退出
├── ShopStock           … 在庫（カード/レリック/ポーション＋価格）と生成
└── CardGridView        … カード一覧/選択（rest-site と共通化）
```

## 主要ロジック

```java
class ShopStock {
    List<Priced<CardId>> cards;
    List<Priced<RelicId>> relics;
    List<Priced<PotionId>> potions;
    int cardRemoveCost = 75;
    static ShopStock generate(Random rng) { /* プールから抽選＋価格付け */ }
}

class ShopScreen {
    void buyCard(Priced<CardId> p) {
        if (rs.spendGold(p.price)) { rs.addCard(p.item); stock.cards.remove(p); }
    }
    void buyRelic(...) { /* spendGold→addRelic→remove */ }
    void buyPotion(...) { /* spendGold→addPotion→remove */ }
    void removeCard(AbstractCard c) {
        if (rs.spendGold(stock.cardRemoveCost)) { rs.removeCard(c); stock.cardRemoveCost += 25; removeUsed = true; }
    }
}
```

## インターフェース

```java
ShopScreen.show(runState, runRng, () -> controller.onNodeResult(COMPLETED));
```

## 依存関係

| ライブラリ / サービス | 用途 |
|-----------------------|------|
| foundation | 画面・入力・描画 |
| run（RunState/RunRng/RunController） | 所持金・所持物・在庫生成・完了通知 |
| content（cards/relics/potions プール） | 在庫の供給 |
| combat/cards | カード表示・削除対象 |

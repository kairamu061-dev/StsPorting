# rest-site（休憩所）設計

## 技術選定

| 技術 | 用途 | 選定理由 |
|------|------|----------|
| libGDX Screen | 焚き火画面 | foundation の画面基盤に準拠 |
| RunState 直接操作 | 回復/強化 | メタ進行のため即時反映 |

## アーキテクチャ

```
com.stsporting.run.restsite
├── RestSiteScreen      … 二択 UI、鍛冶時のデッキ表示
└── (CardGridView 再利用) … デッキ一覧/選択（shop と共通化可）
```

## 主要ロジック

```java
class RestSiteScreen {
    void onRest() {
        int amt = (int)Math.ceil(rs.maxHp() * 0.30);
        rs.heal(amt);
        finish(); // NodeResult(COMPLETED)
    }
    void onSmith(AbstractCard chosen) {
        chosen.upgrade();     // masterDeck 上のインスタンス
        finish();
    }
    List<AbstractCard> upgradableCards() {
        return rs.masterDeck().stream().filter(c -> !c.upgraded && c.canUpgrade()).toList();
    }
}
```

## インターフェース

```java
// run の RunController から起動
RestSiteScreen.show(runState, () -> controller.onNodeResult(COMPLETED));
```

## 依存関係

| ライブラリ / サービス | 用途 |
|-----------------------|------|
| foundation | 画面・入力・描画 |
| run（RunState/RunController） | 状態操作・完了通知 |
| combat/cards（upgrade） | カードアップグレード |
| content/cards | デッキ内カードの強化差分 |

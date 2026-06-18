# treasure（宝箱）設計

## 技術選定

| 技術 | 用途 | 選定理由 |
|------|------|----------|
| libGDX Screen | 宝箱画面 | foundation 準拠 |
| RunRng（treasure 系統） | 中身生成 | 決定的 |

## アーキテクチャ

```
com.stsporting.run.treasure
├── TreasureScreen      … 開封 UI・取得
└── ChestContents       … 中身（relic? + gold? + potion?）の生成
```

## 主要ロジック

```java
class ChestContents {
    RelicId relic; int gold; PotionId potion;
    static ChestContents generate(RunState rs, Random rng) {
        RelicId r = rollRelicExcludingOwned(rs, rng);
        int g = rng.nextInt(100) < GOLD_CHANCE ? smallGold(rng) : 0;
        PotionId p = (rng.nextInt(100) < POTION_CHANCE && rs.hasPotionSlot()) ? rollPotion(rng) : null;
        return new ChestContents(r, g, p);
    }
}

class TreasureScreen {
    void open() {
        if (contents.relic != null) rs.addRelic(contents.relic);
        if (contents.gold > 0) rs.addGold(contents.gold);
        if (contents.potion != null) rs.addPotion(contents.potion);
        finish();
    }
    void leave() { finish(); }
}
```

## インターフェース

```java
TreasureScreen.show(runState, runRng, () -> controller.onNodeResult(COMPLETED));
```

## 依存関係

| ライブラリ / サービス | 用途 |
|-----------------------|------|
| foundation | 画面・入力・描画 |
| run（RunState/RunRng/RunController） | 中身生成・取得・完了通知 |
| content（relics/potions プール） | 抽選元 |

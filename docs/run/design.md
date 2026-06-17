# run 設計

> 本機能エリアはサブ項目に分割済み。親はラン横断の状態・進行・RNG・報酬・HUD を担い、
> 各ノード画面の詳細はサブ項目を参照する。
>
> - [run/rest-site](./rest-site/overview.md) … 休憩所（休息/鍛冶）
> - [run/shop](./shop/overview.md) … ショップ（購入/カード削除）
> - [run/treasure](./treasure/overview.md) … 宝箱
> - [run/event](./event/overview.md) … イベント実行基盤

## 技術選定

| 技術 | 用途 | 選定理由 |
|------|------|----------|
| Java 17 / libGDX | 実装・画面 | foundation と統一 |
| `java.util.Random`（系統別インスタンス） | 決定的 RNG | シードから派生した独立系統で再現性とテスト容易性を確保 |
| イミュータブル更新 + クランプ | RunState 整合性 | HP/金などの不正値を取得時に防止し、テストしやすく |

## アーキテクチャ

```
com.stsporting.run
├── RunState            … 永続状態の保持（HP/金/デッキ/レリック/ポーション/位置/シード）
├── RunController       … ノード解決ループ。NodeType → 子 Screen 起動 → NodeResult 反映
├── RunRng              … シード系統管理（map/cardReward/monster/potion/event/treasure/relic）
├── reward/
│   ├── RewardScreen    … 戦闘報酬 UI（金/カード3択/ポーション/レリック）
│   └── RewardBuilder   … 戦闘種別から報酬内容を抽選
├── hud/
│   └── TopBar          … HP/金/レリック/ポーション/フロアの常時表示・ポーション操作
└── end/
    ├── GameOverScreen
    └── VictoryScreen
```

- **司令塔モデル**: `RunController` がアクティブな子 `Screen`（戦闘/マップ/休憩/ショップ/宝箱/イベント/報酬）を起動し、終了時に `NodeResult` を受けて `RunState` を更新、次の遷移を決める。
- **HUD 共有**: 戦闘外の各画面は `TopBar` を上部に重ねて描画。ポーション使用は `RunState` を介して即時反映。戦闘中の HUD は `combat` 側が持つ（情報源は同じ RunState）。
- **報酬は親責務**: 報酬画面は RunState 更新と密結合のため親に置く（独立 UI だが、抽選〜反映が controller の責務に内包される小さな単位のため分割しない。判断は dev-notes 記載）。

## データ構造

```text
RunState {
  maxHp: int            // 初期 80
  currentHp: int        // 0..maxHp でクランプ
  gold: int             // >= 0
  masterDeck: List<CardInstance>   // content の CardId 参照を持つ実体
  relics: List<RelicId>
  potions: PotionSlot[3]           // null 可（空スロット）
  act: int              // 1..
  floor: int            // 0..
  mapNode: NodePosition // 現在のマップ座標
  seed: long
}

NodeType = MONSTER | ELITE | BOSS | REST | MERCHANT | TREASURE | EVENT
NodeResult {
  outcome: VICTORY | DEFEAT | COMPLETED | FLED
  rewards: RewardBundle?   // 戦闘時のみ
  stateMutations: ...      // ショップ購入・休憩など各画面が適用済みでも可
}

RewardBundle {
  gold: int
  cardChoices: List<CardId>   // 通常3枚、スキップ可
  relic: RelicId?             // エリート/ボス/宝箱
  potion: PotionId?           // 確率
}

PotionSlot { potion: PotionId | EMPTY }
```

RNG 系統（`RunRng`）はシードから `SplittableRandom`/`Random` を系統名ごとに初期化し、消費順が再現されるようにする。

## インターフェース

```java
class RunController {
    void startNewRun(long seed);          // RunState 初期化 → マップへ
    void enterNode(NodePosition pos, NodeType type); // 子画面起動
    void onNodeResult(NodeResult result); // 状態反映 → 次遷移（マップ/報酬/終了）
    RunState state();
}

class RunState {
    void heal(int amount);                // currentHp を maxHp までクランプ
    void loseHp(int amount);              // 0 までクランプ。0 で死亡フラグ
    void addGold(int amount);
    boolean spendGold(int amount);        // 不足なら false
    void addCard(CardId id);
    void removeCard(CardInstance c);
    boolean addPotion(PotionId id);       // 空きが無ければ false
    void addRelic(RelicId id);
    boolean isDead();
}

class RunRng {
    Random stream(RngStream which);       // 系統別の乱数列
}
```

## 依存関係

| ライブラリ / サービス | 用途 |
|-----------------------|------|
| foundation（GameContext/ScreenManager/Assets/Viewport） | 画面起動・共有依存 |
| combat 機能エリア | 戦闘の起動と結果（NodeResult）取得 |
| map 機能エリア | マップ選択画面・現在位置 |
| content 機能エリア | CardId/RelicId/PotionId/モンスター/イベント定義の参照 |
| サブ項目（rest-site/shop/treasure/event） | 各ノード画面の実装 |

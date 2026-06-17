# map 設計

## 技術選定

| 技術 | 用途 | 選定理由 |
|------|------|----------|
| Java 17 / libGDX | 生成・描画・入力 | foundation と統一 |
| 決定的 RNG（RunRng の map 系統） | マップ生成 | 同一シードで同一マップを保証、テスト可能 |

分割しない（単一画面で生成/描画/選択が密結合、単体検証が困難な小規模単位）。判断は dev-notes 参照。

## アーキテクチャ

```
com.stsporting.map
├── MapGenerator    … シードからノードグラフを生成（行/列/エッジ/種別割当/検証）
├── MapGraph        … 生成結果（List<List<MapNode>> と隣接関係）
├── MapNode         … 1 ノード（種別・座標・接続先・訪問済みフラグ）
├── MapScreen       … 描画＋入力。選択可能集合の算出と run への通知
└── MapView         … レイアウト/アイコン/経路線の描画ヘルパ
```

- `MapGenerator.generate(seed, act)` → `MapGraph`。生成後に「最下行から最上ボスへ至る経路が存在する」ことを検証し、不成立なら再生成（決定的に次の試行）。
- `MapScreen` は `RunState.mapNode`（現在位置）から「次行の接続ノード集合」を選択可能としてハイライト。選択で `run` の `onNodeSelected(node)` を呼ぶ。

## データ構造

```text
MapNode {
  row: int            // 0=最下(開始域) .. ROWS-1=ボス
  col: int
  type: NodeType      // MONSTER|ELITE|REST|MERCHANT|TREASURE|EVENT|BOSS
  next: List<MapNode> // 上の行への接続
  prev: List<MapNode>
  visited: boolean
}

MapGraph {
  rows: List<List<MapNode>>
  boss: MapNode
  startNodes: List<MapNode>   // 最下行の選択可能ノード
}

生成パラメータ（初期値・原作に寄せて調整）:
  ROWS = 15
  MIN_PER_ROW = 2, MAX_PER_ROW = 5
  ELITE_MIN_ROW, REST_NEAR_TOP, SHOP/TREASURE 制約 ... (dev-notes で差分管理)
```

## インターフェース

```java
class MapGenerator {
    MapGraph generate(long seed, int act);   // 決定的。検証込み
}

class MapScreen {
    void show(MapGraph graph, MapNode current);
    // 選択時に run へコールバック
    interface Listener { void onNodeSelected(MapNode node); }
}

class MapGraph {
    Set<MapNode> selectableFrom(MapNode current); // current==null なら startNodes
}
```

## 依存関係

| ライブラリ / サービス | 用途 |
|-----------------------|------|
| foundation | 描画・入力・アセット |
| run（RunRng/RunState/RunController） | シード供給・現在位置・選択通知先 |
| content（NodeType の種別配分パラメータ参照は最小限） | ノード種別の定義 |

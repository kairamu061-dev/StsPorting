# enemy 設計

## 技術選定

| 技術 | 用途 | 選定理由 |
|------|------|----------|
| Java / 抽象クラス AbstractMonster | 敵モデル | 原作 AbstractMonster を踏襲。content がサブクラスで各敵を定義 |
| 重み付き抽選 + 履歴判定 | 行動 AI | 原作の確率＋連続制限を表現。RunRng で決定的 |

## アーキテクチャ

```
com.stsporting.combat.creature.enemy
├── AbstractMonster     … Creature 継承。intent/moveHistory/getNextMove/takeTurn
├── Intent              … 種別＋予告ダメージ/回数の表示モデル
├── IntentType (enum)   … ATTACK, ATTACK_MULTI, DEFEND, BUFF, DEBUFF, ATTACK_DEFEND, UNKNOWN
├── EnemyMove           … 1 行動（id, intentType, baseDamage, hits, weight, maxConsecutive, action 生成）
└── MoveSelector        … 候補・履歴から次 move を抽選（連続制限適用）
```

- `AbstractMonster` は `EnemyMove nextMove` と `Deque<String> moveHistory` を持つ。`takeTurn()` は `nextMove` を GameAction に展開してキューへ、その後 `rollNextMove()`。
- content/monsters の各敵サブクラスは `getMoves()`（候補リスト）と HP レンジ・`rollNextMove` のフォールバックを提供。

## データ構造

```java
abstract class AbstractMonster extends Creature {
    Intent intent;
    EnemyMove nextMove;
    Deque<String> moveHistory;   // 直近の move id

    abstract List<EnemyMove> moves();    // content が定義（重み・連続上限つき）
    abstract void rollNextMove(Random rng); // 既定は MoveSelector + フォールバック

    void takeTurn(ActionManager mgr, Player player) {
        nextMove.enqueue(mgr, this, player); // 行動を GameAction 化
    }

    Intent buildIntent(Player player) {
        // 攻撃なら 表示ダメージ = applyVulnerable(player, applyStrength(this, base)) * hits 情報
    }
}

class EnemyMove {
    String id;
    IntentType type;
    int baseDamage, hits;
    int weight;            // 抽選重み
    int maxConsecutive;    // 連続上限
    void enqueue(ActionManager mgr, AbstractMonster self, Player p); // 行動の実体
}

class MoveSelector {
    EnemyMove select(List<EnemyMove> moves, Deque<String> history, Random rng) {
        // history 末尾の連続回数が maxConsecutive 以上の move を除外し、重みで抽選
    }
}
```

### 予告ダメージ計算

```java
int intentDamage(AbstractMonster self, Player player, int base) {
    int d = base;
    for (AbstractPower p : self.powers)   d = p.atDamageGive(d, ATTACK);     // 敵の筋力
    for (AbstractPower p : player.powers) d = p.atDamageReceive(d, ATTACK);  // プレイヤーの脆弱
    return Math.max(0, d);
}
```

> DamageAction と同じ修正関数を使い、予告と実ダメージが一致するようにする（ブロック前の値を表示）。

## インターフェース

```java
class AbstractMonster {
    void initialize(Random rng);    // HP ロール・初期 rollNextMove
    void takeTurn(ActionManager mgr, Player player);
    void rollNextMove(Random rng);
    Intent intent();
}
```

## 依存関係

| ライブラリ / サービス | 用途 |
|-----------------------|------|
| combat 親（Creature） | HP/ブロック/パワーの基底 |
| action-queue（DamageAction/GainBlockAction/ApplyPowerAction） | takeTurn の効果 |
| powers | 予告/実ダメージの修正・敵バフ |
| turn-flow | 敵ターンでの takeTurn 呼び出し |
| content/monsters | 各敵の moves・HP・フォールバック |
| run（RunRng） | 行動選択の決定的乱数 |
| effects | インテントアイコン描画（論理はここ） |

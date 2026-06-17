# combat 設計

> サブ項目に分割済み。親は戦闘全体のアーキテクチャ・境界（CombatRequest/Result）・
> 解決順序の規約を担う。各システムの詳細は以下を参照。
>
> - [combat/action-queue](./action-queue/overview.md) … アクションキュー（解決順序の根幹）
> - [combat/turn-flow](./turn-flow/overview.md) … ターン進行・エネルギー
> - [combat/cards](./cards/overview.md) … カードと各種山札・プレイ処理
> - [combat/powers](./powers/overview.md) … パワー（バフ/デバフ）
> - [combat/enemy](./enemy/overview.md) … 敵・インテント・AI
> - [combat/input](./input/overview.md) … ターゲティング・操作性
> - [combat/effects](./effects/overview.md) … 演出・アニメーション

## 技術選定

| 技術 | 用途 | 選定理由 |
|------|------|----------|
| Java 17 / libGDX | 実装・描画・入力 | foundation と統一。原作と同一エンジン |
| アクションキュー（自作・原作準拠） | 効果の逐次解決 | 原作 GameActionManager のモデルを踏襲し、順序/タイミングを完全再現 |
| ロジック/描画の分離 | テスト容易性 | 戦闘状態機械をヘッドレスで検証可能にする |
| 決定的 RNG（RunRng 由来） | 敵 AI・シャッフル | 再現性とユニットテスト |

## アーキテクチャ

```
com.stsporting.combat
├── CombatScreen        … 戦闘画面。update→アクションキュー駆動→描画
├── CombatState         … 戦闘中の状態（プレイヤー/敵リスト/手札/山札/捨て札/消滅/エネルギー/ターン数）
├── action/
│   ├── ActionManager   … キュー本体。current/常駐/ターン制御アクションの解決ループ
│   └── GameAction      … 個々の処理単位（DamageAction, GainBlockAction, DrawAction, ApplyPowerAction...）
├── card/               … [combat/cards] カードモデル・piles・CardQueueItem（プレイ要求）
├── power/              … [combat/powers] AbstractPower・スタック・フック
├── creature/
│   ├── Creature        … 共通基底（HP/ブロック/パワー）
│   ├── Player          … プレイヤー（RunState 由来の HP/デッキ/レリック/ポーション）
│   └── Enemy           … [combat/enemy] 敵・インテント・moveHistory
├── input/              … [combat/input] ドラッグ/ターゲティング/ホバー
└── vfx/                … [combat/effects] エフェクト・数値・シェイク
```

### 解決順序の規約（システム完全再現の根幹）

原作のアクションキューを踏襲し、以下を守る:

1. プレイヤーの操作（カードプレイ等）は **CardQueueItem** としてキューに積まれ、即時には完了しない。
2. `ActionManager` は毎フレーム「現在のアクション」を 1 つ取り出して `update()` を回し、完了したら次へ進む。
3. アクションは実行中に **別のアクションをキュー先頭へ割り込み挿入** できる（例: ダメージ → on-attacked パワー誘発 → 追加ダメージ）。割り込みは LIFO 的に先頭へ積まれ、先に解決される。
4. ターン開始/終了・パワーの増減など定常処理も専用アクションとして同じキューで解決し、演出と状態変化の順序を一致させる。
5. ダメージ計算順: `攻撃側の攻撃修正（筋力 +、弱体 ×0.75）` → `被弾側の被ダメ修正（脆弱 ×1.5）` → `ブロック軽減` → `HP 減算` → `被弾フック（とげ/中毒解決等）`。

### 戦闘境界（run との契約）

```text
CombatRequest {
  player: PlayerSnapshot     // 現在/最大HP, masterDeck(コピー), relics, potions
  encounter: EncounterDef    // 敵編成（content の MonsterId とHP/AIパターン）
  rng: RunRng                // 決定的乱数（combat 系統）
  rewardTier: NORMAL|ELITE|BOSS
}

CombatResult {
  outcome: VICTORY | DEFEAT
  endingHp: int              // 戦闘終了時HP（run の RunState に反映）
  usedPotions: List<PotionId>
  // デッキ自体は戦闘内ではコピーを操作し、永続変更（カード追加等）は報酬で行う
}
```

戦闘中はマスターデッキの**コピー**で山札を構成し、戦闘での消滅/生成は戦闘内に閉じる。永続的なデッキ変更は `run` の報酬・ショップ・休憩で行う。

## データ構造

```text
CombatState {
  player: Player
  enemies: List<Enemy>
  drawPile, hand, discardPile, exhaustPile: List<CardInstance>
  energy: int
  turn: int
  phase: BEGIN | PLAYER | ENEMY | END
}

Creature {
  currentHp, maxHp: int
  block: int
  powers: List<AbstractPower>   // 付与順を保持
  isDead(): boolean
}
```

詳細な型（GameAction の種類、AbstractPower のフック、Enemy のインテント等）は各サブ項目の design.md を参照。

## インターフェース

```java
class CombatScreen {            // run が起動
    void begin(CombatRequest req);
    CombatResult result();      // 終了時に取得
}

class ActionManager {
    void addToBottom(GameAction a);  // 通常追加（末尾）
    void addToTop(GameAction a);     // 割り込み（先頭）
    void update();                   // 1フレーム分の解決を進める
    boolean isEmpty();
}

abstract class GameAction {
    boolean isDone;
    abstract void update();     // 自身の処理。必要なら addToTop で割り込み
}
```

## 依存関係

| ライブラリ / サービス | 用途 |
|-----------------------|------|
| foundation | 画面・描画・入力・アセット基盤 |
| run（RunState/RunRng/報酬） | 戦闘の起動元・結果反映先・決定的乱数 |
| content | カード/敵/パワー/レリックの定義データ |
| 各 combat サブ項目 | 戦闘内システムの実装 |

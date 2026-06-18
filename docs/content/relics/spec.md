# relics（content）仕様

## 機能一覧

| # | 機能名 | 説明 |
|---|--------|------|
| 1 | レリック定義 | RelicId・名称・フック実装・説明 |
| 2 | フック接続 | combat/run の各タイミングへ接続 |
| 3 | RelicLibrary | RelicId → 生成・登録 |
| 4 | 取得処理 | run の addRelic で取得時フック発火・HUD 反映 |

## フック一覧（AbstractRelic）

| フック | 発火元 | 用途例 |
|--------|--------|--------|
| `onEquip()` | run.addRelic（取得時） | 最大HP増・初期ポーション等の即時効果 |
| `atBattleStart()` | combat（戦闘開始） | 開幕ブロック/バフ/とげ付与 |
| `atTurnStart()` | combat/turn-flow（ターン頭） | 毎ターンのブロック/エネルギー |
| `onUseCard(card)` | combat/cards（プレイ時） | プレイ枚数カウント系効果 |
| `onAttacked(info,dmg)` / `onAttack(...)` | combat（DamageAction） | 被弾/攻撃反応 |
| `onVictory()` | combat 終了（勝利） | バーニングブラッドの HP+6 |
| `atTurnEnd()` | combat（ターン終わり） | 終了時効果 |

> レリックは GameAction を積むか、直接 RunState/CombatState を操作（取得時の最大HP増等）する。戦闘中効果は action-queue 経由を基本とする。

## 代表レリックの効果

| レリック | フック | 効果 |
|----------|--------|------|
| Burning Blood | onVictory | プレイヤー HP+6（RunState.heal） |
| Blood Vial | atBattleStart | 戦闘開始時 HP+2 |
| Anchor | atBattleStart | 初回ターンにブロック+10 |
| Bronze Scales | atBattleStart | Thorns 3 を自身に付与 |
| Vajra | atBattleStart | Strength +1 |
| Pen Nib 系 | onUseCard | 攻撃を数えて N 回目の攻撃ダメージ2倍（カウントはレリックが保持） |
| 取得時HP系 | onEquip | RunState.maxHp += n（現在HPも +n） |

## エラーケース

| 条件 | 挙動 |
|------|------|
| 同一レリック重複取得 | 原則発生しない（取得元で除外）。発生時は無視 |
| onVictory で HP が最大超過 | RunState.heal がクランプ |
| カウント系のリセット忘れ | 戦闘/ターン境界で適切にリセット（atBattleStart/atTurnStart で初期化） |

## 未対応ケース

- 全レリック網羅
- 複雑な相互作用（条件付き連鎖）は段階対応

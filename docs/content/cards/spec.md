# cards（content）仕様

## 機能一覧

| # | 機能名 | 説明 |
|---|--------|------|
| 1 | 初期デッキ定義 | Strike/Defend/Bash の生成 |
| 2 | 報酬カード定義 | コモン中心の代表カード群 |
| 3 | アップグレード | 各カードの + 差分 |
| 4 | レアリティ/プール | BASIC/COMMON/UNCOMMON/RARE の分類と報酬抽選用プール |
| 5 | CardLibrary | CardId → 新規インスタンス生成・登録 |

## カード効果の表現方針

各カードは `use(player, target)` で combat のアクションを積む。例：

| カード | use() の中身（積むアクション） |
|--------|-------------------------------|
| Strike | `addToBottom(new DamageAction(target, upgraded?9:6, player, ATTACK))` |
| Defend | `addToBottom(new GainBlockAction(player, upgraded?8:5))` |
| Bash | `DamageAction(target, upgraded?10:8)` → `ApplyPowerAction(target, Vulnerable, upgraded?3:2)` |
| Cleave | 全敵へ `DamageAction(e, upgraded?11:8)`（ALL_ENEMY） |
| Iron Wave | `DamageAction(target, 5/7)` ＋ `GainBlockAction(player, 5/7)` |
| Body Slam | `DamageAction(target, player.block)` |
| Pommel Strike | `DamageAction(target, 9/10)` ＋ `DrawAction(1/2)` |
| Flex | `ApplyPowerAction(player, Strength, 2/4)` ＋（ターン終わりに戻す一時筋力ロジック） |
| Inflame | `ApplyPowerAction(player, Strength, 2/3)`（POWER：永続、カード消費） |
| Metallicize | `ApplyPowerAction(player, Metallicize, 3/4)`（POWER） |
| Anger | `DamageAction(target, 6/8)` ＋ 自身のコピーを discardPile へ生成 |

## レアリティとプール

| レアリティ | 用途 |
|-----------|------|
| BASIC | 初期デッキ（報酬には基本出ない） |
| COMMON | 報酬で高頻度 |
| UNCOMMON | 報酬で中頻度 |
| RARE | 報酬で低頻度（初期は少数） |

報酬抽選（run の RewardBuilder）はレアリティ確率に従い、アイアンクラッド/共通プールから 3 枚を重複なく選ぶ。

## エラーケース

| 条件 | 挙動 |
|------|------|
| Body Slam でブロック0 | 0 ダメージ（DamageAction が 0 を許容） |
| 全体カードで敵が1体 | その1体に適用 |
| プール不足（再現対象が少ない） | 抽選可能数だけ提示、または run 側でスキップ可能 |
| 一時筋力（Flex）のターン戻し | ターン終わりに同量の筋力を減算するパワー/フックで実装 |

## 未対応ケース

- 上記以外のカードの網羅
- 特殊な生成/変身カード（段階対応）
- 呪い/状態異常カードの全種

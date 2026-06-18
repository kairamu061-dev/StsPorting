# potions（content）仕様

## 機能一覧

| # | 機能名 | 説明 |
|---|--------|------|
| 1 | ポーション定義 | PotionId・名称・対象要否・効果・レアリティ |
| 2 | 使用処理 | use(target) で action-queue へ効果投入、スロット消費 |
| 3 | 対象選択連携 | 対象ありは input の対象選択を経て使用 |
| 4 | PotionLibrary | PotionId → 生成・登録 |

## 効果の表現方針

| ポーション | use() の中身 |
|------------|-------------|
| 回復 | `RunState.heal(n)` または `HealAction`（戦闘中） |
| ブロック | `GainBlockAction(player, 12)` |
| 筋力 | `ApplyPowerAction(player, Strength, 2)` |
| 弱体（敵） | `ApplyPowerAction(target, Weak, 3)` |
| 脆弱（敵） | `ApplyPowerAction(target, Vulnerable, 3)` |
| エナジー | `EnergyManager.add(2)` |
| ドロー | `DrawAction(3)` |
| 炎（敵） | `DamageAction(target, 20, player, THORNS/POTION)` |

## 使用フロー

1. input がポーションスロットクリック→「使用」
2. `needsTarget` なら敵選択（input）、不要なら即時
3. `potion.use(player, target)` が action-queue へ効果を積む
4. スロットから除去（消費）。HUD 更新

## エラーケース

| 条件 | 挙動 |
|------|------|
| 戦闘外で戦闘専用ポーション使用 | 使用不可（戦闘中のみ）。または無効表示 |
| 対象ありを対象なしで使用 | 対象選択を要求、未選択ならキャンセル |
| エネ/ドロー系を戦闘外で使用 | 無効（戦闘リソースのため） |
| スロット満杯で取得 | run 側で取得スキップ/破棄選択（run のポーション管理） |

## 未対応ケース

- 全ポーション網羅
- 投擲/特殊条件ポーションは段階対応

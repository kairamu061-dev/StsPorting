# cards 仕様

## 機能一覧

| # | 機能名 | 説明 |
|---|--------|------|
| 1 | CardInstance | 定義参照＋戦闘内可変状態（コスト変更/アップグレード/一時効果） |
| 2 | piles 管理 | drawPile/hand/discardPile/exhaustPile の保持と移動 |
| 3 | ドロー | 山札から手札へ。山札切れで捨て札シャッフル。手札上限 10 |
| 4 | プレイ可否 | コスト充足＋プレイ可能条件（プレイ不可カードの拒否） |
| 5 | プレイ実行 | コスト消費 → use() で効果アクション投入 → 使用後移動 |
| 6 | 使用後移動 | ATTACK/SKILL→捨て札（消滅指定は exhaust）、POWER→消費 |
| 7 | コスト変動 | 一時 0 コスト・X コスト・コスト増減 |
| 8 | アップグレード | 戦闘内アップグレードと表示/効果差分 |

## カードのライフサイクル

```
drawPile --draw--> hand --play(可否OK)--> (コスト消費) --use()--> 効果アクション群
   ^                                                         |
   |                                                         v
   +--shuffle-- discardPile <----- 使用後(ATTACK/SKILL) ----+
                                   使用後(exhaust指定) ----> exhaustPile
                                   使用後(POWER) ----------> 消費(消える)
```

## プレイ処理の詳細

1. input が「カードをプレイ」（対象付き）を要求 → `CardQueueItem(card, target)` を action-queue へ
2. action-queue がアイドル時に取り出し、`canPlay(card, target)` を判定
   - エネルギー不足／プレイ不可カード／対象不正 → キャンセル（手札へ戻す、フィードバック）
3. `spendEnergy(cost)`（X コストは spendAll）
4. カードを hand から取り除き、`card.use(player, target)` を呼ぶ（content 実装が GameAction を addToBottom/addToTop）
5. 使用後の移動：
   - POWER：盤面にパワー付与後、カードは消費（どの pile にも残らない）
   - exhaust 指定：exhaustPile へ（onExhaust フック）
   - それ以外：discardPile へ
6. 「カードプレイ時」フック（レリック/パワー：例 攻撃時◯）を発火

## ドロー処理の詳細

- `draw(n)`：n 回繰り返し。drawPile が空なら discardPile を RunRng でシャッフルして drawPile に戻してから引く
- 手札が 10 枚なら以降は引かない（引いたカードは捨て札へ、原作準拠の上限処理）
- drawPile も discardPile も空なら引けない（何もしない）

## エラーケース

| 条件 | 挙動 |
|------|------|
| エネルギー不足でプレイ | キャンセル。手札へ戻す。赤フィードバック（input/effects） |
| プレイ不可カード（呪い/状態異常）をプレイ | 拒否（一部は「プレイ不可だが手札を埋める」挙動） |
| 対象必須カードを対象なしでプレイ | キャンセル |
| 手札上限超過のドロー | 上限で停止（原作準拠） |
| 山札・捨て札とも空でドロー | 何もしない |
| exhaust 対象が既に無い | 無視 |

## 未対応ケース

- 各カードの個別効果・数値（content/cards）
- 特殊な保持（Retain）/コスト恒久変更/手札以外からのプレイ等は段階対応
- カード移動のアニメーション（effects）

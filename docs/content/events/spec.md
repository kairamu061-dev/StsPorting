# events（content）仕様

## 機能一覧

| # | 機能名 | 説明 |
|---|--------|------|
| 1 | イベント定義 | EventId・本文・選択肢・各選択の結果 |
| 2 | 選択肢の条件 | 所持金/HP/カード所持などで選択可否を判定 |
| 3 | 結果適用 | RunState 変化 or 戦闘起動要求を返す |
| 4 | EventLibrary | EventId → 生成・登録 |

## 選択肢と結果の表現

```
EventOption {
  text: String
  enabled(RunState): boolean        // 条件（金不足等で不可）
  apply(RunState): EventOutcome     // 結果（状態変化 or 戦闘要求）
}
EventOutcome = STAY(続き表示) | LEAVE(マップへ) | START_COMBAT(encounter) | NEXT(別ページ)
```

結果の例（apply 内の操作）：

| 結果 | 操作 |
|------|------|
| 最大HP増 | `rs.maxHp += n; rs.heal(n)` |
| 全回復 | `rs.heal(rs.maxHp)` |
| レリック入手 | `rs.addRelic(id)` |
| 呪い付与 | `rs.addCard(CURSE_id)` |
| 所持金増減 | `rs.addGold(±n)` / `rs.spendGold(n)` |
| カード入手/削除 | `rs.addCard(id)` / カード選択 UI → `rs.removeCard` |
| 戦闘 | `return START_COMBAT(encounter)` |

## 代表イベントの構成（例：Big Fish）

```
本文: 川に3つの餌が浮かんでいる。
選択:
  - 餌A（マンゴー）: 最大HP+? → LEAVE
  - 餌B（ドーナツ）: 全回復 → LEAVE
  - 餌C（箱）: レリック入手＋呪い1枚 → LEAVE
```

## エラーケース

| 条件 | 挙動 |
|------|------|
| 条件不足の選択肢 | 非活性表示（enabled=false） |
| カード削除対象がデッキにない | 該当選択肢を非表示/非活性 |
| 戦闘誘発後の結果適用 | 戦闘勝利後に残り報酬を適用（run/event が戻りを処理） |
| 未実装結果 | ログ警告し状態を変えずに LEAVE（段階実装中の安全策） |

## 未対応ケース

- 全イベント網羅・多段分岐の全パターン
- ランダム結果（確率分岐）の細則は段階対応

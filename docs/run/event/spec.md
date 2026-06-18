# event（イベント実行基盤）仕様

## 機能一覧

| # | 機能名 | 説明 |
|---|--------|------|
| 1 | イベント抽選 | content/events プールから RunRng で1件選択（決定的） |
| 2 | 提示 | 本文・選択肢の描画。条件で選択肢を非活性 |
| 3 | 選択適用 | EventOption.apply(RunState) 実行→EventOutcome |
| 4 | 結果分岐 | STAY/LEAVE/START_COMBAT/NEXT に応じ遷移 |
| 5 | カード選択連携 | 入手/削除時の CardGridView 呼び出し |
| 6 | 戦闘往復 | START_COMBAT→combat→CombatResult で続き |

## 操作フロー

1. イベントノード進入 → `EventLibrary` から抽選したイベントを表示
2. 選択肢のうち `enabled(rs)` を満たすものを活性表示
3. 選択 → `outcome = option.apply(rs)`
4. outcome 分岐：
   - LEAVE → NodeResult(COMPLETED) でマップ
   - STAY / NEXT → 続き本文・別選択肢を表示
   - START_COMBAT → combat 起動 → 戻りで残り処理 → 最終的に LEAVE
5. カード入手/削除を伴う場合は CardGridView で選択

## エラーケース

| 条件 | 挙動 |
|------|------|
| 全選択肢が非活性 | 「立ち去る」既定肢を必ず用意（詰み防止） |
| 未実装イベント抽選 | ログ警告し COMPLETED で素通り（段階実装の安全策） |
| 戦闘誘発後にプレイヤー死亡 | combat 側でゲームオーバー（イベントへ戻らない） |
| カード選択キャンセル | 該当結果を取り消し or 既定処理（イベント定義に従う） |

## 未対応ケース

- 多段分岐の複雑なイベント全種（content/events 側の対応に追従）
- ランダム結果の演出（最小再現）

# action-queue 開発メモ

## 実装上の判断

| 判断内容 | 理由 |
|----------|------|
| 完了したアクションを 1 段階で null に戻し、次フレームで次を取り出す | アクションが積んだ割り込み（addToTop）を必ず「次に」解決させ、原作の誘発順を再現するため。即座に次を取り出すと割り込みが後回しになる |
| addToTop は「最後に積んだものが先に解決」 | Deque の先頭挿入そのまま。複数割り込みの順序規約を明文化し、正順で入れたい場合は逆順 addToTop かまとめアクションを使う |
| 演出待ちを duration（delta 消化）で表現 | フレームレート非依存。ロジックと演出待ちを同一機構に載せ、原作の「演出が終わるまで次に進まない」挙動を再現 |
| ロジックを描画から分離しヘッドレステスト可能に | 解決順・ダメージ計算・割り込み連鎖の収束を決定的に検証するため |
| 入力受付は isIdle 時のみ | キュー解決中の多重入力を防ぎ、原作の「演出中は操作不可」を再現 |

## 発生した問題と対処

| 問題 | 対処 |
|------|------|
| action-queue と powers が相互依存（DamageAction が修正値を powers から取得、Thorns が DamageAction を割り込み生成） | 両者を同時に実装。AbstractPower 基底＋コアパワー（筋力/弱体/脆弱/とげ/中毒/金属化/再生）を action-queue と一括で実装し、DamageAction の解決順をテストで担保 |
| カード pile 系アクション（Draw/Discard/Exhaust）は AbstractCard 前提 | cards サブ項目に委譲。action-queue 段階では Creature/パワー系アクションに限定し、cardQueue/queueCard も cards 実装時に追加 |

## 設計からの変更点

| 変更内容 | 理由 |
|----------|------|
| `update()` は cardQueue を未実装（action queue のみ） | cards 未実装のため。設計の cardQueue/queueCard は cards サブ項目で追加する（API 互換で拡張予定） |
| 実テスト用ヘルパ `runToCompletion()`（delta=1f で収束まで回す）を ActionManager に追加 | ヘッドレスで解決順・割り込み・duration を決定的に検証するため。無限ループ防止のガード付き |
| コアパワーを action-queue と同時に combat/powers に実装 | 相互依存のため。powers サブ項目のタスクの一部を前倒しで完了（powers/tasks に反映予定） |

## 今後の課題

- 死亡対象に対する解決中効果の扱い（原作の細則）を実装時に詰める
- レリックの戦闘中フック（ターン頭/被弾時/カードプレイ時）を本機構のどのフック点に差し込むか、powers/relics 実装時に確定
- ゲーム速度（高速モード）対応は任意。duration の delta スケールで吸収予定

## ユーザへの要望

- （現状なし）

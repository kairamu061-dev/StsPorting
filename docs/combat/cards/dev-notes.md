# cards 開発メモ

## 実装上の判断

| 判断内容 | 理由 |
|----------|------|
| 使用後移動を PostPlayAction として効果アクションの後に積む | 「効果解決 → カード移動」の順序を原作と一致させる。効果中にカードがまだ手札参照可能な挙動も再現可能 |
| プレイは CardQueueItem 経由（即時適用しない） | action-queue の解決順に乗せ、演出/誘発の順序を統一 |
| コスト一時変更（costForTurn/freeToPlayOnce）を可変状態で保持 | 0 コスト化等の効果を表現し、ターン頭で baseCost に戻す原作挙動を再現 |
| POWER カードは pile に残さず消費 | 原作仕様。盤面にパワー付与後カードは消える |
| 仕組み（cards）と効果（content/cards）を分離 | カードのライフサイクルを汎用化し、content が use() で個別効果を表現 |

## 発生した問題と対処

| 問題 | 対処 |
|------|------|
| pile アクション（Draw/Discard/Exhaust）を action と card のどちらに置くか（循環依存懸念） | これらは AbstractCard を操作するため card パッケージに配置（card→action の単方向）。action パッケージは card を import しない。設計では action/common に置く想定だったが、依存方向を整理して card 側へ |
| `queueCard`/cardQueue を ActionManager に持たせると action→card 依存が生じる | ActionManager には持たせず、`PlayCardFlow.resolve(mgr, card, target)` を入力層から直接呼ぶ方式に。CardQueueItem は入力層用の小ホルダとして card パッケージに用意 |

## 設計からの変更点

| 変更内容 | 理由 |
|----------|------|
| 4 pile を `CardPiles` クラスでなく `CombatState` のフィールドに直接保持 | 戦闘状態の一部として一元管理する方が素直で、アクションからの参照も簡潔 |
| エネルギー消費は当面 `CombatState.energy` を直接操作 | turn-flow の EnergyManager 未実装のため。X コストと共に turn-flow 実装時に EnergyManager へ移譲 |
| `use(target, mgr)` シグネチャ（mgr をフィールドでなく引数で渡す） | カードを mgr 非依存のステートレスに保ち、content 実装をシンプルにする |

## 今後の課題

- 手札保持（Retain）・コスト恒久変更・手札以外からのプレイなど特殊挙動は段階対応
- onExhaust/onDiscard などカード側フックの一覧を content 実装時に拡充
- ステータス/呪いカード（isPlayable=false 系）の扱いを content と擦り合わせ

## ユーザへの要望

- （現状なし）

# powers 仕様

## 機能一覧

| # | 機能名 | 説明 |
|---|--------|------|
| 1 | パワー付与 | ApplyPowerAction でクリーチャーに付与/スタック加算 |
| 2 | スタック規則 | 同種加算・上限・0 で消滅・ターン減衰 |
| 3 | ダメージ修正フック | atDamageGive（与ダメ修正）/ atDamageReceive（被ダメ修正） |
| 4 | 被弾/与ダメフック | onAttacked（とげ等）/ onAttack（与ダメ後） |
| 5 | ターン境界フック | atTurnStart / atTurnEnd（中毒/再生/金属化等） |
| 6 | カード/ブロックフック | onUseCard / onGainBlock 等の補助フック |
| 7 | コアパワー実装 | 筋力/弱体/脆弱/中毒/金属化/とげ/再生 ほか |

## フック一覧と発火タイミング

| フック | 呼び出し元 | 用途例 |
|--------|-----------|--------|
| `atDamageGive(dmg, type)` | DamageAction（攻撃側パワー） | 筋力 +amount、弱体 ×0.75 |
| `atDamageReceive(dmg, type)` | DamageAction（被弾側パワー） | 脆弱 ×1.5 |
| `onAttacked(info, dmg)` | DamageAction（被弾側、HP 減算後） | とげ：addToTop で反撃ダメージ |
| `onAttack(info, dmg, target)` | DamageAction（攻撃側、適用後） | 与ダメ系誘発 |
| `atStartOfTurn()` | turn-flow（atTurnStart） | 再生：HP 回復、燃焼/中毒の自傷判定 |
| `atEndOfTurn(isPlayer)` | turn-flow（atTurnEnd） | 中毒：HP-amount＋amount-1、金属化：ブロック付与 |
| `atStartOfTurnPostDraw()` | turn-flow（ドロー後） | 一部開始時効果 |
| `onUseCard(card)` | cards（プレイ時） | カード関連トリガー |
| `onGainBlock(amount)` | GainBlockAction | ブロック増加トリガー |
| `modifyBlock(amount)` | GainBlockAction | 付与ブロックの修正 |

> 同一フックに複数パワーが反応する場合、付与順（powers リスト順）に呼ぶ。順序依存（筋力→弱体→脆弱）は DamageAction 側の呼び出し順で担保。

## コアパワー仕様

| パワー | 種別 | 効果 | 減衰/消滅 |
|--------|------|------|----------|
| 筋力 Strength | バフ | atDamageGive: +amount（攻撃のみ） | 永続（負値も可＝筋力低下） |
| 弱体 Weak | デバフ | atDamageGive: ×0.75（攻撃、切り捨て） | ターン終わりに -1、0 で消滅 |
| 脆弱 Vulnerable | デバフ | atDamageReceive: ×1.5（攻撃、切り捨て） | ターン終わりに -1、0 で消滅 |
| 中毒 Poison | デバフ | atStartOfTurn: HP-amount、その後 amount-1 | 自然減で 0 消滅 |
| 金属化 Metallicize | バフ | atEndOfTurn: ブロック +amount | 永続 |
| とげ Thorns | バフ | onAttacked: 攻撃者へ amount ダメージ（addToTop） | 永続 |
| 再生 Regen | バフ | atStartOfTurn: HP +amount、amount-1（プレイヤー系） | 自然減 |

> 弱体/脆弱の減衰タイミング（ターン終わり）はプレイヤー/敵で対象が異なる点に注意（自分のターン終わりに自分のデバフを減らす原作仕様に合わせる）。

## エラーケース

| 条件 | 挙動 |
|------|------|
| 負のスタックになる付与 | 筋力は負値許容（筋力低下）。デバフ系は 0 下限でクランプ |
| 既に死亡したクリーチャーへの付与 | 無視 |
| 切り捨て/丸め | 弱体0.75・脆弱1.5 は乗算後に切り捨て（原作準拠）。適用順で結果が変わるため順序固定 |
| 同種パワーの重複付与 | amount を加算（別インスタンスを増やさない） |

## 未対応ケース

- 全パワー網羅（content 連携で代表を順次追加）
- アーティファクト（デバフ無効化）等の高度な相互作用は段階対応

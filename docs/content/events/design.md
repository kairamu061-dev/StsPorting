# events（content）設計

## 技術選定

| 技術 | 用途 | 選定理由 |
|------|------|----------|
| AbstractEvent / EventOption | イベントモデル | 本文・選択肢・結果を構造化 |
| EventId enum / EventLibrary | 識別・生成 | 参照と登録 |

> イベントは分岐ロジックを伴うためコード定義（純データより表現力）を採用。run/event が UI と入力を担う。

## アーキテクチャ

```
com.stsporting.content.events
├── EventId (enum)      … BIG_FISH, GOLDEN_IDOL, CURSED_TOME, ...
├── AbstractEvent       … pages/options、現在ページ、apply 結果
├── EventOption         … text/enabled/apply
├── EventOutcome (enum) … STAY, LEAVE, START_COMBAT, NEXT
├── EventLibrary        … EventId -> Supplier、newEvent(id)
└── impl/
```

## 実装例

```java
abstract class AbstractEvent {
    EventId id; String body;
    abstract List<EventOption> options(RunState rs);
}

class BigFish extends AbstractEvent {
    List<EventOption> options(RunState rs) {
        return List.of(
            opt("マンゴー（最大HP+）", r -> { r.increaseMaxHp(8); return LEAVE; }),
            opt("ドーナツ（全回復）",  r -> { r.heal(r.maxHp()); return LEAVE; }),
            opt("箱（レリック＋呪い）", r -> { r.addRelic(randomRelic(r)); r.addCard(CardId.CURSE_REGRET); return LEAVE; })
        );
    }
}
```

`EventOption.enabled(rs)` で金/HP/カード条件を判定し、run/event が非活性表示。`apply` の戻り値で run/event が次挙動（マップへ/続き/戦闘）を決める。

## インターフェース

```java
class EventLibrary { static void register(); static AbstractEvent newEvent(EventId id); }
// run/event が:
//   event.options(rs) を描画 → 選択 → outcome = option.apply(rs) → outcome で分岐
```

## 依存関係

| ライブラリ / サービス | 用途 |
|-----------------------|------|
| run/event（実行基盤） | 提示・入力・結果分岐 |
| run（RunState） | 状態変化の適用先 |
| content（cards/relics/potions） | 入手/付与対象の参照 |
| combat | 戦闘誘発（START_COMBAT） |
| content（ContentRegistry） | 登録・参照 |

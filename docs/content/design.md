# content 設計

> サブ項目に分割済み。親はデータ参照規約・レジストリ・ID 体系を担う。
>
> - [content/cards](./cards/overview.md)
> - [content/relics](./relics/overview.md)
> - [content/potions](./potions/overview.md)
> - [content/monsters](./monsters/overview.md)
> - [content/events](./events/overview.md)

## 技術選定

| 技術 | 用途 | 選定理由 |
|------|------|----------|
| Java（コードによる定義） | 各データ定義 | カード/敵の効果は GameAction の組み立てを伴うため、データ＋ふるまいをコードで表現する方が忠実かつ型安全 |
| レジストリ（ID→定義） | 参照解決 | combat/run/map から ID で疎結合に参照。段階追加が容易 |
| enum/定数の ID 体系 | 識別子 | CardId/RelicId/PotionId/MonsterId/EventId をタイプセーフに |

> 純データ（数値のみ）の部分は将来 JSON 外出しも可能だが、初期は効果ロジックと一体のコード定義を優先（原作の効果を忠実に再現するため）。

## アーキテクチャ

```
com.stsporting.content
├── ContentRegistry     … 全定義の登録/取得（起動時に各 *Library が登録）
├── ids/                … CardId, RelicId, PotionId, MonsterId, EventId
├── cards/   (sub)      … AbstractCard 実装群 + CardLibrary
├── relics/  (sub)      … AbstractRelic 実装群 + RelicLibrary
├── potions/ (sub)      … AbstractPotion 実装群 + PotionLibrary
├── monsters/(sub)      … AbstractMonster 実装群 + Encounters + MonsterLibrary
└── events/  (sub)      … AbstractEvent 実装群 + EventLibrary
```

- 各サブ項目は `register()` でレジストリに自身を登録。`run`/`combat`/`map` は `ContentRegistry.card(id)` 等で取得。
- カード/敵/レリックの効果は `combat` の `GameAction`・`AbstractPower` を組み立てて表現（content は combat の仕組みに乗る）。

## データ構造

```text
ContentRegistry {
  card(CardId): CardDef
  relic(RelicId): RelicDef
  potion(PotionId): PotionDef
  monster(MonsterId): MonsterDef
  event(EventId): EventDef
  encounter(NodeType, act, rng): EncounterDef
}

CardDef (抽象 AbstractCard) {
  id, name, type(ATTACK|SKILL|POWER), cost, rarity, target
  use(player, target): void        // GameAction をキューに積む
  upgraded: boolean / upgrade(): void
}

MonsterDef (抽象 AbstractMonster) {
  id, hpRange
  rollMove(rng, history): Intent    // AI（確率・連続制限）
  takeTurn(): void                   // インテントに応じた GameAction
}
```

各サブ項目 design.md で具体クラスと効果を定義する。

## インターフェース

```java
class ContentRegistry {
    static AbstractCard newCard(CardId id);
    static AbstractRelic newRelic(RelicId id);
    static AbstractPotion newPotion(PotionId id);
    static AbstractMonster newMonster(MonsterId id);
    static AbstractEvent newEvent(EventId id);
    static EncounterDef rollEncounter(NodeType type, int act, Random rng);
}
```

## 依存関係

| ライブラリ / サービス | 用途 |
|-----------------------|------|
| combat（GameAction/AbstractPower/Creature） | 効果の表現基盤 |
| run（RunState/報酬プール） | 初期デッキ/レリック・報酬抽選の参照元 |
| map（NodeType） | エンカウンター/イベント割当 |

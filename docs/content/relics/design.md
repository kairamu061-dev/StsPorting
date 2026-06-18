# relics（content）設計

## 技術選定

| 技術 | 用途 | 選定理由 |
|------|------|----------|
| AbstractRelic（フック基底） | レリックモデル | 原作 AbstractRelic 同様、必要フックのみ override |
| RelicId enum / RelicLibrary | 識別・生成 | タイプセーフ参照と登録 |

## アーキテクチャ

```
com.stsporting.content.relics
├── RelicId (enum)      … BURNING_BLOOD, BLOOD_VIAL, ANCHOR, BRONZE_SCALES, VAJRA, ...
├── AbstractRelic       … 全フック既定 no-op。owner(RunState/Player) 参照
├── RelicLibrary        … RelicId -> Supplier<AbstractRelic>、newRelic(id)
├── RelicManager        … プレイヤー保持レリックのフック一括発火（combat/run から呼ぶ）
└── impl/               … 各レリック実装
```

- `RelicManager` はプレイヤーの保持レリック集合を持ち、combat/run の各タイミングで該当フックを順に呼ぶ（取得順）。combat は atBattleStart/atTurnStart/onUseCard/onAttacked/onVictory を、run は onEquip を呼ぶ。

## 実装例

```java
abstract class AbstractRelic {
    RelicId id; String name;
    void onEquip(RunState rs) {}
    void atBattleStart(CombatState cs, ActionManager mgr) {}
    void atTurnStart(CombatState cs, ActionManager mgr) {}
    void onUseCard(AbstractCard c, CombatState cs, ActionManager mgr) {}
    void onVictory(RunState rs) {}
}

class BurningBlood extends AbstractRelic {
    void onVictory(RunState rs) { rs.heal(6); }
}
class Anchor extends AbstractRelic {
    void atBattleStart(CombatState cs, ActionManager mgr) {
        mgr.addToBottom(new GainBlockAction(cs.player, 10)); // 初回ターン分
    }
}
class BronzeScales extends AbstractRelic {
    void atBattleStart(CombatState cs, ActionManager mgr) {
        mgr.addToBottom(new ApplyPowerAction(cs.player, new ThornsPower(), 3));
    }
}
class PenNib extends AbstractRelic {
    int count = 0;
    void atBattleStart(CombatState cs, ActionManager mgr) { count = 0; }
    void onUseCard(AbstractCard c, CombatState cs, ActionManager mgr) {
        if (c.type == ATTACK && ++count >= 10) { /* 次攻撃2倍フラグ */ count = 0; }
    }
}
```

## インターフェース

```java
class RelicLibrary { static void register(); static AbstractRelic newRelic(RelicId id); }
class RelicManager {
    void add(AbstractRelic r, RunState rs);   // onEquip 発火
    void atBattleStart(CombatState, ActionManager);
    void atTurnStart(CombatState, ActionManager);
    void onUseCard(AbstractCard, CombatState, ActionManager);
    void onVictory(RunState);
}
```

## 依存関係

| ライブラリ / サービス | 用途 |
|-----------------------|------|
| combat（action-queue/powers/CombatState） | 戦闘中効果 |
| run（RunState/取得処理/TopBar） | 取得時効果・HUD |
| content（ContentRegistry） | 登録・参照 |

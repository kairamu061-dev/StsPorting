# potions（content）設計

## 技術選定

| 技術 | 用途 | 選定理由 |
|------|------|----------|
| AbstractPotion | ポーションモデル | use()/needsTarget を持つ抽象基底 |
| PotionId enum / PotionLibrary | 識別・生成 | 参照と登録 |

## アーキテクチャ

```
com.stsporting.content.potions
├── PotionId (enum)     … BLOCK, STRENGTH, WEAK, FEAR, ENERGY, SWIFT, FIRE, HEAL, ...
├── AbstractPotion      … needsTarget / combatOnly / use(player, target)
├── PotionLibrary       … PotionId -> Supplier、newPotion(id)
└── impl/
```

## 実装例

```java
abstract class AbstractPotion {
    PotionId id; String name; boolean needsTarget; boolean combatOnly = true;
    abstract void use(Player p, Creature target, ActionManager mgr, RunState rs);
}

class StrengthPotion extends AbstractPotion {
    StrengthPotion(){ needsTarget=false; }
    void use(Player p, Creature t, ActionManager mgr, RunState rs){
        mgr.addToBottom(new ApplyPowerAction(p, new StrengthPower(), 2));
    }
}
class WeakPotion extends AbstractPotion {
    WeakPotion(){ needsTarget=true; }
    void use(Player p, Creature t, ActionManager mgr, RunState rs){
        mgr.addToBottom(new ApplyPowerAction(t, new WeakPower(), 3));
    }
}
class HealPotion extends AbstractPotion {
    HealPotion(){ needsTarget=false; combatOnly=false; }
    void use(Player p, Creature t, ActionManager mgr, RunState rs){ rs.heal(healAmount()); }
}
```

## インターフェース

```java
class PotionLibrary { static void register(); static AbstractPotion newPotion(PotionId id); }
// 使用は run のポーション管理が呼ぶ:
//   potion.use(player, target, actionManager, runState); slots.remove(potion);
```

## 依存関係

| ライブラリ / サービス | 用途 |
|-----------------------|------|
| combat（action-queue/powers/EnergyManager） | 効果の実体 |
| run（RunState/スロット/TopBar） | 保持・使用・破棄・HUD |
| combat/input | 対象選択 |
| content（ContentRegistry） | 登録・参照 |

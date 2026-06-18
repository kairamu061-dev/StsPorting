# powers 設計

## 技術選定

| 技術 | 用途 | 選定理由 |
|------|------|----------|
| Java / 抽象クラス AbstractPower | パワーモデル | 原作 AbstractPower のフックモデルを踏襲。content がサブクラス追加可 |
| フックメソッドのデフォルト実装 | 拡張性 | 反応しないフックは何もしない既定で、各パワーは必要なフックのみ override |

## アーキテクチャ

```
com.stsporting.combat.power
├── AbstractPower       … amount/type/owner と各フック（デフォルト no-op）
├── PowerType (enum)    … BUFF, DEBUFF
├── PowerHooks          … DamageAction/turn-flow/cards から呼ぶ集約ヘルパ
└── impl/
    ├── StrengthPower / WeakPower / VulnerablePower
    ├── PoisonPower / MetallicizePower / ThornsPower / RegenPower
    └── ...（content 連携で追加）
```

- クリーチャーは `List<AbstractPower> powers` を付与順に保持。
- `PowerHooks` がフック横断の呼び出し（例：`atDamageGive` を攻撃側の全パワーに順に適用）を提供し、DamageAction/turn-flow がこれを呼ぶ。

## データ構造

```java
abstract class AbstractPower {
    PowerId id;
    String name;
    PowerType type;     // BUFF / DEBUFF
    int amount;         // スタック
    Creature owner;

    // ダメージ修正（戻り値で加工）
    int atDamageGive(int dmg, DamageType type) { return dmg; }
    int atDamageReceive(int dmg, DamageType type) { return dmg; }
    int modifyBlock(int amount) { return amount; }

    // 誘発（必要なら mgr.addToTop でアクション割り込み）
    void onAttacked(DamageInfo info, int dmgDealt) {}
    void onAttack(DamageInfo info, int dmgDealt, Creature target) {}
    void onUseCard(AbstractCard c) {}
    void onGainBlock(int amount) {}

    // ターン境界
    void atStartOfTurn() {}
    void atEndOfTurn(boolean isPlayerTurn) {}

    // スタック操作
    void stack(int delta) { amount += delta; }   // 加算
    boolean shouldRemove() { return type==DEBUFF && amount<=0; } // デバフ0で消滅
    void reducePerTurn() {}  // 弱体/脆弱: amount-- → 0で消滅
}
```

### 主要パワーの実装例

```java
class StrengthPower extends AbstractPower {       // BUFF, 永続
    int atDamageGive(int dmg, DamageType t) {
        return (t == ATTACK) ? dmg + amount : dmg;
    }
}
class WeakPower extends AbstractPower {            // DEBUFF
    int atDamageGive(int dmg, DamageType t) {
        return (t == ATTACK) ? (int)Math.floor(dmg * 0.75f) : dmg;
    }
    void reducePerTurn() { amount--; }
}
class VulnerablePower extends AbstractPower {      // DEBUFF
    int atDamageReceive(int dmg, DamageType t) {
        return (t == ATTACK) ? (int)Math.floor(dmg * 1.5f) : dmg;
    }
    void reducePerTurn() { amount--; }
}
class PoisonPower extends AbstractPower {          // DEBUFF
    void atStartOfTurn() {
        mgr.addToBottom(new LoseHpAction(owner, amount)); // ブロック無視
        amount--;
    }
}
class ThornsPower extends AbstractPower {          // BUFF
    void onAttacked(DamageInfo info, int dmg) {
        if (info.attacker != null)
            mgr.addToTop(new DamageAction(info.attacker, amount, owner, THORNS));
    }
}
```

## フック呼び出し順（PowerHooks）

```java
int applyDamageGive(Creature attacker, int dmg, DamageType t) {
    for (AbstractPower p : attacker.powers) dmg = p.atDamageGive(dmg, t);
    return dmg;
}
int applyDamageReceive(Creature target, int dmg, DamageType t) {
    for (AbstractPower p : target.powers) dmg = p.atDamageReceive(dmg, t);
    return dmg;
}
// DamageAction はこの順で呼ぶ: give → receive → block → hp → onAttacked
```

## インターフェース

```java
class ApplyPowerAction extends GameAction { /* 付与/スタック加算、shouldRemoveで掃除 */ }
class ReducePowerAction extends GameAction { /* amount 減算、0で除去 */ }
// turn-flow が atTurnStart/atTurnEnd 時に powers の対応フックを呼ぶアクションを積む
```

## 依存関係

| ライブラリ / サービス | 用途 |
|-----------------------|------|
| action-queue（DamageAction/LoseHpAction） | 修正適用・誘発割り込み |
| turn-flow | ターン境界フックの発火・減衰タイミング |
| cards | onUseCard フック |
| content | 追加パワーの定義 |
| effects | パワーアイコン更新通知 |

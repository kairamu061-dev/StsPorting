# effects 設計

## 技術選定

| 技術 | 用途 | 選定理由 |
|------|------|----------|
| libGDX SpriteBatch / ShapeRenderer | 描画 | 数値/フラッシュ/パーティクルの軽量描画 |
| 寿命付きエフェクトのプール | 管理 | 大量生成を GC 負荷なく扱う |
| delta 連動 + イージング | 時間進行 | フレームレート非依存・原作テンポ |
| カメラオフセットでシェイク | 画面揺れ | Viewport カメラを一時的に揺らす |

## アーキテクチャ

```
com.stsporting.combat.vfx
├── EffectManager       … List<AbstractEffect> の更新/描画/破棄、生成 API
├── AbstractEffect      … duration/elapsed/update(delta)/render(batch)/isDone
├── effects/
│   ├── DamageNumberEffect / BlockNumberEffect / HealNumberEffect
│   ├── HitFlashEffect / ShakeCreatureEffect / SlashParticleEffect
│   ├── GainBlockSparkEffect
│   └── CardMoveEffect（from→to の補間）
├── ScreenShake         … カメラ揺れ（強度/減衰）
├── PowerIconRenderer   … creature.powers を読み描画
├── IntentRenderer      … enemy.intent を読み描画
└── HandRenderer        … input 状態（hover/drag/target）を読み手札/矢印描画
```

- `EffectManager.add(effect)` で生成。`update(delta)` で全エフェクトを進め、`isDone` を破棄。`render` で z 順に描画。
- action-queue のアクションは `vfx.spawnXxx(...)` を呼び、自身の `duration` を演出時間に合わせる。effects は CombatState を読み取り専用で参照。

## データ構造

```java
abstract class AbstractEffect {
    float duration, elapsed;
    boolean isDone;
    void update(float delta) { elapsed += delta; if (elapsed >= duration) isDone = true; }
    abstract void render(SpriteBatch batch);
    protected float progress() { return Math.min(1f, elapsed / duration); } // 0..1
}

class DamageNumberEffect extends AbstractEffect {
    float x, y; int amount; Color color;
    void render(batch) {
        float p = progress();
        float yy = y + p * RISE;            // 上昇
        float a = 1f - p;                   // フェード
        drawNumber(batch, amount, x, yy, color, a);
    }
}

class ScreenShake {
    float intensity, time;
    Vector2 offset(); // カメラに加算するオフセット（time で減衰）
    void shake(float intensity, float time);
}
```

## アクション連携 API

```java
class CombatVfx {
    void onDamage(Creature target, int amount, boolean blocked, DamageType type); // フラッシュ+揺れ+数値+shake
    void onGainBlock(Creature c, int amount);
    void onDraw(AbstractCard c, Vector2 from, Vector2 to);
    void onMoveCard(AbstractCard c, Pile from, Pile to);
    void onApplyPower(Creature c, AbstractPower p, int amount);
    float estimatedDuration(EffectKind kind, int magnitude); // アクションが待ち時間に使う
}
```

> アクション側は `float d = vfx.onDamage(...); this.duration = d;` のように演出時間を受け取り、解決の待ちに反映する。

## レンダリング順（z 順）

1. 背景
2. 敵・プレイヤースプライト（被弾フラッシュ/揺れ適用）
3. パワーアイコン・インテント・HP/ブロックバー
4. パーティクル・斬撃
5. 手札（input 状態で拡大/前面、ドラッグ中カード、対象矢印）
6. 数値ポップ（最前面）
7. （カメラに ScreenShake オフセットを適用）

## 依存関係

| ライブラリ / サービス | 用途 |
|-----------------------|------|
| foundation（SpriteBatch/Viewport/Assets） | 描画・カメラ・アセット |
| action-queue | 演出要求の発火・duration 連携 |
| powers / enemy / cards / input | 描画する論理値（読み取り専用） |
| run（TopBar との整合） | HUD 情報源（同じ RunState/CombatState） |

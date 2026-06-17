# foundation 設計

## 技術選定

| 技術 | 用途 | 選定理由 |
|------|------|----------|
| Java 17 | 実装言語 | libGDX / jpackage が安定対応する LTS。原作と同系統 |
| libGDX 1.12.x | ゲームエンジン | 原作と同一エンジン。`Game`/`Screen`、`AssetManager`、`Viewport` を活用 |
| LWJGL3 backend | デスクトップ起動 | libGDX の現行デスクトップバックエンド。Windows/Linux 両対応 |
| Gradle (wrapper) | ビルド | libGDX 標準。`gradlew` で環境非依存にビルド |
| jpackage (JDK同梱) | Windows 配布 | JRE 同梱の .exe / インストーラを生成。追加依存不要 |
| JUnit 5 + headless | テスト | `HeadlessApplication` でロジックを GUI なし検証（Linux CI 可） |

## アーキテクチャ

```
com.stsporting
├── StsGame              (extends com.badlogic.gdx.Game) … アプリ本体・Screen 切替
├── lwjgl3/DesktopLauncher … LWJGL3 起動・ウィンドウ設定（エントリポイント）
├── core/
│   ├── Assets           … AssetManager ラッパ。ロード/取得/解放、プレースホルダ供給
│   ├── ScreenManager    … Screen の push/pop/replace と遷移
│   ├── InputRouter      … InputProcessor。マウス/キーをアクティブ画面へ供給
│   └── GameContext      … 共有依存（Assets, SpriteBatch, Viewport, settings）の保持
├── render/
│   └── ViewportConfig   … FitViewport(1920x1080)・カメラ・レターボックス
└── screens/
    ├── BootScreen       … アセットロード進捗表示 → MainMenu へ
    ├── MainMenuScreen   … タイトル＋ボタン。新規ラン/設定(任意)/終了
    └── PlaceholderGameScreen … 後続機能エリアが差し替えるダミー
```

- **ライフサイクル**: `DesktopLauncher` → `StsGame.create()` で `GameContext` 構築 → `BootScreen` を設定 → ロード後 `MainMenuScreen`。
- **描画/更新分離**: 各 `Screen.render(delta)` 内で `update(delta)` → `draw()` の順。`delta` 駆動でフレームレート非依存。
- **入力**: `InputRouter` を `Gdx.input.setInputProcessor` に登録し、現在の `Screen`（が実装する `InputConsumer`）へ委譲。ヒットテストは仮想座標へ `Viewport.unproject` してから判定。
- **画面差し替え点**: `MainMenuScreen` の「新規ラン開始」は本フェーズでは `PlaceholderGameScreen` を開く。`run` 機能エリア実装時にラン開始処理へ差し替える（差し替え1点で接続できるよう設計）。

## データ構造

```text
GameContext {
  batch: SpriteBatch          // 全画面共有の描画バッチ
  viewport: Viewport          // FitViewport(VIRTUAL_W=1920, VIRTUAL_H=1080)
  camera: OrthographicCamera
  assets: Assets
  screenManager: ScreenManager
  settings: Settings          // 任意。音量等。未使用ならデフォルト値
}

Assets {
  manager: AssetManager
  load(): void                // 共通アセットをキューしロード
  get<T>(path): T             // 取得。失敗時はプレースホルダを返す
  placeholderTexture(): Texture  // マゼンタ 1x1（欠落代替）
  defaultFont(): BitmapFont   // フォント欠落時のフォールバック
  dispose(): void
}

Settings {
  masterVolume: float = 1.0
  fullscreen: boolean = false
  // 永続化は任意・後続
}
```

仮想解像度定数：`VIRTUAL_WIDTH = 1920`, `VIRTUAL_HEIGHT = 1080`（原作準拠）。

## インターフェース

```java
// シーンの最小契約（libGDX Screen を踏襲しつつ入力を一本化）
interface InputConsumer {
    boolean onTouchDown(float vx, float vy, int button); // 仮想座標
    boolean onTouchUp(float vx, float vy, int button);
    boolean onMouseMoved(float vx, float vy);
    boolean onKeyDown(int keycode);
}

class ScreenManager {
    void replace(Screen next);   // 現在画面を破棄し置換
    void push(Screen overlay);   // オーバーレイ（中断メニュー等）
    void pop();                  // オーバーレイを閉じる
    Screen current();
}

class Assets {
    void queueCommon();          // 共通アセットをロードキューに積む
    boolean update();            // 進捗を進める（true=完了）
    float progress();            // 0.0–1.0
    <T> T get(String path, Class<T> type);
    void dispose();
}

// 起動側（LWJGL3）
class DesktopLauncher {
    static void main(String[] args); // Lwjgl3ApplicationConfiguration を構成し StsGame を起動
}
```

ウィンドウ設定（`Lwjgl3ApplicationConfiguration`）: タイトル「Slay the Spire (再現)」、初期 1280×720、リサイズ可、VSync 有効、リサイズ時は `Viewport.update(w,h,true)` を `StsGame.resize` から呼ぶ。

## 依存関係

| ライブラリ / サービス | 用途 |
|-----------------------|------|
| com.badlogicgames.gdx:gdx | コア API（Game/Screen/グラフィクス/入力） |
| com.badlogicgames.gdx:gdx-backend-lwjgl3 | デスクトップ実行バックエンド |
| com.badlogicgames.gdx:gdx-platform (natives-desktop) | LWJGL3 ネイティブ |
| com.badlogicgames.gdx:gdx-freetype (+natives) | TrueType フォントのランタイム生成（任意） |
| org.junit.jupiter:junit-jupiter | ユニットテスト |
| com.badlogicgames.gdx:gdx-backend-headless | テストの GUI レス実行 |
| JDK 17 (jpackage 同梱) | Windows .exe 生成 |

# foundation タスク

## 実装タスク一覧

<!-- ステータス: [ ] 未着手 / [~] 進行中 / [x] 完了 -->

- [x] 開発環境セットアップ（JDK 17・Gradle wrapper の導入）
- [x] Gradle プロジェクト初期化（libGDX 依存・ソースセット・LWJGL3 backend）
- [x] `DesktopLauncher`（LWJGL3）でウィンドウ起動
- [x] `StsGame`（ApplicationAdapter）と `GameContext` の構築
- [x] `ViewportConfig`：FitViewport(1920×1080)＋カメラ＋レターボックス
- [x] `Assets`：AssetManager ラッパ・プレースホルダ供給・解放
- [x] `InputRouter` / `InputConsumer`：入力の一元化と仮想座標変換
- [x] `ScreenManager`：replace/push/pop と遷移
- [x] `BootScreen`：ロード進捗表示 → MainMenu 遷移
- [x] `MainMenuScreen`：タイトル＋ボタン（新規ラン/設定(任意)/終了）・ホバー/押下
- [x] `PlaceholderGameScreen`：ダミー画面＋ESC で戻る
- [x] ヘッドレステスト基盤＋ ScreenManager のユニットテスト（4 件 PASS）
- [ ] Assets のヘッドレステスト（GL 依存部分は実機/headless backend で別途）
- [ ] jpackage による Windows .exe 生成手順の文書化
- [ ] `gradlew run` での GUI 起動確認（Windows 実機）

## 依存関係

- 開発環境セットアップ → 以降の全タスクの前提
- Gradle プロジェクト初期化 → DesktopLauncher / StsGame
- GameContext → Assets / ViewportConfig / ScreenManager（共有依存の保持先）
- ViewportConfig・InputRouter → 各 Screen（描画・入力の前提）
- ScreenManager・MainMenuScreen → PlaceholderGameScreen 遷移
- 全実装 → jpackage 文書化（最後）

## ステータス

In progress（骨格実装＋ヘッドレステスト完了。GUI 起動確認と jpackage 手順が残）

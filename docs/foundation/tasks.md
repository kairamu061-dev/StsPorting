# foundation タスク

## 実装タスク一覧

<!-- ステータス: [ ] 未着手 / [~] 進行中 / [x] 完了 -->

- [ ] 開発環境セットアップ（JDK 17・Gradle wrapper の導入）
- [ ] Gradle プロジェクト初期化（libGDX 依存・ソースセット・LWJGL3 backend）
- [ ] `DesktopLauncher`（LWJGL3）でウィンドウ起動
- [ ] `StsGame`（Game）と `GameContext` の構築
- [ ] `ViewportConfig`：FitViewport(1920×1080)＋カメラ＋レターボックス
- [ ] `Assets`：AssetManager ラッパ・プレースホルダ供給・解放
- [ ] `InputRouter` / `InputConsumer`：入力の一元化と仮想座標変換
- [ ] `ScreenManager`：replace/push/pop と遷移
- [ ] `BootScreen`：ロード進捗表示 → MainMenu 遷移
- [ ] `MainMenuScreen`：タイトル＋ボタン（新規ラン/設定(任意)/終了）・ホバー/押下
- [ ] `PlaceholderGameScreen`：ダミー画面＋ESC で戻る
- [ ] ヘッドレステスト基盤（HeadlessApplication）＋ Assets / ScreenManager のユニットテスト
- [ ] jpackage による Windows .exe 生成手順の文書化

## 依存関係

- 開発環境セットアップ → 以降の全タスクの前提
- Gradle プロジェクト初期化 → DesktopLauncher / StsGame
- GameContext → Assets / ViewportConfig / ScreenManager（共有依存の保持先）
- ViewportConfig・InputRouter → 各 Screen（描画・入力の前提）
- ScreenManager・MainMenuScreen → PlaceholderGameScreen 遷移
- 全実装 → jpackage 文書化（最後）

## ステータス

Todo（ドキュメント整備完了。実装は開発環境セットアップ後に着手）

# foundation 開発メモ

## 実装上の判断

| 判断内容 | 理由 |
|----------|------|
| libGDX + Java を採用 | 原作と同一エンジン。フレーム挙動・入力・エフェクトを忠実に再現でき、デコンパイル資料が全て Java で参照しやすい（ユーザ承認済み） |
| foundation を分割せず一体で扱う | アプリ骨格（Game/Screen・Viewport・Assets・Input）は密結合で、いずれも単体検証できず、メインメニューはその骨格を動作確認する最初の小さな画面。分割ルールの例外（単体検証不可かつ極小）に該当 |
| 仮想解像度 1920×1080 / FitViewport | 原作準拠の座標系。レターボックスでアスペクト比を保ち、UI レイアウトを解像度非依存にする |
| 入力を InputConsumer に一本化 | 後続の戦闘でドラッグ操作など細かな入力タイミング再現が必要。最初から仮想座標で受ける共通経路に集約しておく |
| 「新規ラン開始」を差し替え1点に設計 | foundation 段階では PlaceholderGameScreen を開き、run 機能エリア実装時にそこだけ差し替えて接続できるようにする |

## 発生した問題と対処

| 問題 | 対処 |
|------|------|
| `add-feature.sh` が CRLF 改行で実行エラー（`$'\r'`） | 当該スクリプトを LF に正規化。`.gitattributes` の `* text=auto eol=lf` で今後の混入も抑止 |
| git 初期化時に dubious ownership / not in a git directory | `safe.directory` 追加と `GIT_DISCOVERY_ACROSS_FILESYSTEM=1` で解消（マウント境界越えの discovery が原因） |

## 設計からの変更点

| 変更内容 | 理由 |
|----------|------|
| `StsGame` を libGDX `Game` でなく `ApplicationAdapter`＋自作 `GameScreen`/`ScreenManager` に | libGDX の `Screen` は GL 前提でスタックlogic を単体テストしづらい。自作の軽量 `GameScreen` にすることで ScreenManager をヘッドレスで決定的にテスト可能にした（4 件 PASS） |
| ビルドは単一 Gradle モジュール（core/lwjgl3 分割なし） | デスクトップ専用のため分割不要。`com.stsporting.lwjgl3.DesktopLauncher` を application mainClass に |
| Gradle は toolchain でなく sourceCompatibility=17 | 稼働 JVM が JDK17 のため、toolchain 自動プロビジョニング（ネットワーク取得）を避けて確実にビルド |
| UI ラベルは当面 ASCII（"New Run" 等） | バンドルの libGDX 標準フォントに日本語グリフが無く豆腐になるため。日本語フォント導入までの暫定 |
| Gradle 8.7 を採用（apt 版は古い） | libGDX 1.12.1 は Gradle 8 系が必要。Dockerfile でバイナリ配布を導入 |

## 今後の課題

- 設定（音量・解像度・フルスクリーン）の永続化は任意・後続
- jpackage での .exe 生成は Windows 上での実行検証が必要（Linux 開発環境ではビルド/テストまで）
- 原作アセット非同梱のため、最終的な見た目の完全一致には差し替え用の自作/代替アセット整備が別途必要

## ユーザへの要望

- **開発環境**: 解決済み。Dockerfile に JDK 17・Gradle 8.7 を追加し、稼働コンテナにも導入。Gradle wrapper（`gradlew`）同梱済みで `./gradlew test` がヘッドレスで通る。
- **実機確認**: GUI を伴う起動確認（`gradlew run` / 生成 .exe）は Windows 側で実施が必要。Linux 開発環境ではコンパイル＋ヘッドレステストまでを担保。
  - Windows での確認手順: リポジトリを clone → `gradlew.bat run`（JDK 17 が必要）。ウィンドウにメインメニューが出れば OK。

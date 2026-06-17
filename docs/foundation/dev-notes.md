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
| （なし：実装未着手） | — |

## 今後の課題

- 設定（音量・解像度・フルスクリーン）の永続化は任意・後続
- jpackage での .exe 生成は Windows 上での実行検証が必要（Linux 開発環境ではビルド/テストまで）
- 原作アセット非同梱のため、最終的な見た目の完全一致には差し替え用の自作/代替アセット整備が別途必要

## ユーザへの要望

- **開発環境**: この devcontainer には JDK / Gradle が未導入。実装着手には JDK 17 と Gradle（wrapper 同梱予定）の導入が必要。devcontainer の Dockerfile に追加してよいか確認したい。
- **実機確認**: GUI を伴う最終動作確認は Windows 側で行う必要がある。ビルド成果物（.exe または `gradlew run`）を Windows で実行できる環境の用意をお願いしたい。

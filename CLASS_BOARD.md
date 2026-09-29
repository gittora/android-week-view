# Class Board

画像を参考にした、青と白の大学向け時間割アプリです。Android Week View のサンプルアプリを起点に、時限単位の Compose 画面を実装しています。

- 月〜土・1〜5限の時間割。初回は画像を参考にしたサンプル授業を表示します。
- 学期名をタップすると前期・後期を切り替えます。
- マスをタップして授業名・教室・担当教員を登録、編集、削除できます。
- 授業と選択中の学期は端末内に保存します。

Android Studio でこのフォルダを開き、`app` を実行してください。元プロジェクトに合わせて Android SDK 37 と Java 17 ツールチェーンを使用します。APK のビルドは `gradlew.bat :app:assembleDebug` です。

画面は `app/src/main/java/de/tobiasschuerg/weekview/sample/TimetableScreen.kt`、保存処理は同じフォルダの `TimetableStore.kt` にあります。現時点では曜日・時限数・開始時刻は固定です。通知、学校システム連携、クラウド同期は含みません。

元プロジェクト: https://github.com/tobiasschuerg/android-week-view （MIT。LICENSEを保持）

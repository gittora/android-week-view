# Class Board

画像を参考にした、青と白の大学向け時間割アプリです。Android Week View のサンプルアプリを起点に、時限単位の Compose 画面を実装しています。

- 月〜土・1〜5限の時間割。初回は画像を参考にしたサンプル授業を表示します。
- 学期名をタップすると First（前期）・Second（後期）を切り替えます。保存済みデータは従来どおり使えます。
- マスをタップして授業名・教室・担当教員を登録、編集、削除できます。
- 授業と選択中の学期は端末内に保存します。
- 学期名の横の「Setting」から、表示曜日（月〜金・月〜土・月〜日）、時限数（4〜8限）、各時限の開始時刻を変更できます。
- 開始時刻は `09:00` のような24時間表記で、早い順に入力します。設定は全学期共通で端末内に保存します。
- 表示する曜日・時限を減らしても授業は削除されず、表示を戻すと再び確認できます。

Android Studio でこのフォルダを開き、`app` を実行してください。元プロジェクトに合わせて Android SDK 37 と Java 17 ツールチェーンを使用します。APK のビルドは `gradlew.bat :app:assembleDebug` です。

画面は `app/src/main/java/de/tobiasschuerg/weekview/sample/TimetableScreen.kt`、設定画面は `TimetableSettingsDialog.kt`、保存処理は `TimetableStore.kt` にあります。通知、学校システム連携、クラウド同期は含みません。

元プロジェクト: https://github.com/tobiasschuerg/android-week-view （MIT。LICENSEを保持）

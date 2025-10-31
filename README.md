# OnStop2FinishAndRemoveTask

|before|after|
|---|---|
| <video controls src="https://github.com/user-attachments/assets/ecdc5050-f05b-4c98-8554-dc765f65cb93"></video> | <video controls src="https://github.com/user-attachments/assets/e7c4cfa0-0173-408c-bed0-7c6b09234507"></video> |

別のアプリに切り替えた時、切り替え前のアプリを自動的に終了するアプリです。アプリは登録する必要があります。
`Shizuku`が必要です。

If change other app, Auto close task in prev app. The app must be registered. Required
`Shizuku APP`.

# ダウンロード / download
GitHub Release

https://github.com/takusan23/OnStop2FinishAndRemoveTask/releases

# 仕組み / How to work

`Android アプリ開発者`ですか？  
以下のコードのような動作をします。これを好きなアプリで利用することができるアプリです。

You're `Android app developer ?`  
This app behavior like this code. Maybe, this app is available behavior for any app.

```kotlin
class MainActivity : ComponentActivity() {
    override fun onStop() {
        super.onStop()
        finishAndRemoveTask()
    }
}
```

フォアグラウンドサービスから今表示されている`Activity`を監視します。別のアプリに切り替わったところでタスクを終了する関数を呼び出しています。  
これらの関数は内部の関数なので、それらを呼び出すために`Shizuku`を利用しています。

Observe current Activity From Foreground service. If change other app, Invoke remove task function.  
this APIs Invoke by `Shizuku`. that's because, using API is Internal-API.

# ビルド方法 / building

最新の`Android Studio`でビルド可能です。

Available build in latest Android Studio.

## ビルド手順 / step

https://github.com/Reginer/aosp-android-jar

隠し`API`を利用するため、`android.jar`を上記の`URL`からダウンロードして入れ替える必要があります。

This app is using Hidden-API. Must download `android.jar` in this URL. and replace `android.jar`.

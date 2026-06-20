# aidl

- https://cs.android.com/android/platform/superproject/+/android-latest-release:frameworks/base/core/java/android/app/IActivityManager.aidl
- https://cs.android.com/android/platform/superproject/+/android-latest-release:frameworks/base/core/java/android/app/ITransientNotificationCallback.aidl
- etc...

`AOSP`から`Shizuku`で呼び出したい関数をコピーペーストしています。

# これは何
`android.jar`を差し替える代わりに、`Shizuku`で呼び出すメソッドだけを`AOSP`からコピーしてきたもの。

コピーしたコードはあくまでもビルドを通すためで、インストール後は`Android`に入っているクラスが呼び出されます！！！

# aidl の場合
`IActivityManager`とかは、`java`コードが自動生成されるため`AOSP`では`.aidl`しかありません。  
`build.gradle.kts`で`aidl=true`しているため、`Java`から呼び出すときに使う`Stub...`が生成されます。

# aidl で Parcelable のクラスを使う場合
`Parcelable`クラスと`Java`で書いたあと、`aidl`にも同じパッケージと名前で作って、中で`parcelable {クラス名}`することで`aidl`側でクラスを参照できるようになります。  
`Java`側は`android.app`見たくパッケージを`AOSP`にそろえる必要があります。

# Java のメソッド、変数の場合
メソッドの場合は名前と引数とかはコピーするものの、中の実装は適当に例外を投げてビルドを通せばよさそう。  
パッケージ名は上に同じく合わせてください。
package io.github.takusan23.onstop2finishandremovetask.tool

import android.app.IActivityManager
import android.app.INotificationManager
import android.content.pm.PackageManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.SystemServiceHelper
import kotlin.coroutines.resume

/** Shizuku 経由で なんとかManager を叩く */
object ShizukuServiceTool {

    val activity
        get() = IActivityManager.Stub.asInterface(
            ShizukuBinderWrapper(SystemServiceHelper.getSystemService("activity"))
        )

    val notification
        get() = INotificationManager.Stub.asInterface(
            ShizukuBinderWrapper(SystemServiceHelper.getSystemService("notification"))
        )

    /** Shizuku が有効になるまで一時停止する。Shizuku アプリが有効になるまで呼ばれない。 */
    suspend fun awaitShizuku() {
        // addBinderReceivedListenerSticky が二度呼ばれることがあるみたいで、ここだけ Flow にしておく
        callbackFlow {
            val listener = object : Shizuku.OnBinderReceivedListener {
                override fun onBinderReceived() {
                    Shizuku.removeBinderReceivedListener(this)
                    trySend(Unit)
                }
            }
            Shizuku.addBinderReceivedListenerSticky(listener)
            awaitClose { Shizuku.removeBinderReceivedListener(listener) }
        }.first()
    }

    /** Shizuku 権限が付与されるのを待つ */
    suspend fun awaitShizukuPermission() {
        awaitShizuku()

        if (checkShizukuPermission()) {
            return
        }

        suspendCancellableCoroutine { continuation ->
            val listener = object : Shizuku.OnRequestPermissionResultListener {
                override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        Shizuku.removeRequestPermissionResultListener(this)
                        continuation.resume(Unit)
                    }
                }
            }
            Shizuku.addRequestPermissionResultListener(listener)
            continuation.invokeOnCancellation { Shizuku.removeRequestPermissionResultListener(listener) }
        }
    }

    /** Shizuku の権限があれば true を返す */
    suspend fun checkShizukuPermission(): Boolean {
        awaitShizuku()
        return Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
    }
}
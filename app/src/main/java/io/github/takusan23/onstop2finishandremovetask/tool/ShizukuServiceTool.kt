package io.github.takusan23.onstop2finishandremovetask.tool

import android.app.IActivityManager
import android.app.INotificationManager
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.SystemServiceHelper

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

}
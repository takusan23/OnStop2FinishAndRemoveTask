package io.github.takusan23.onstop2finishandremovetask.tool

import android.app.IActivityManager
import android.app.INotificationManager
import android.os.ServiceManager
import rikka.shizuku.ShizukuBinderWrapper

/** Shizuku 経由で なんとかManager を叩く */
object ShizukuServiceTool {

    val activity
        get() = IActivityManager.Stub.asInterface(
            ShizukuBinderWrapper(ServiceManager.getService("activity"))
        )

    val notification
        get() = INotificationManager.Stub.asInterface(
            ShizukuBinderWrapper(ServiceManager.getService("notification"))
        )

}
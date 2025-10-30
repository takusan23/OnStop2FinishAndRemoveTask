package io.github.takusan23.onstop2finishandremovetask

import android.app.ActivityManager
import android.app.ITaskStackListener
import android.app.ITransientNotificationCallback
import android.app.NotificationManager
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.widget.Toast
import android.window.TaskSnapshot
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.github.takusan23.onstop2finishandremovetask.tool.RegisterAppListTool
import io.github.takusan23.onstop2finishandremovetask.tool.ShizukuServiceTool
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/** 今表示されている Activity が離れた（ほかアプリに切り替えた）時に Activity を終了する */
class OnStop2FinishAndRemoveTaskService : Service() {

    private val scope = MainScope()

    private val notificationManager by lazy { NotificationManagerCompat.from(this) }

    /** 最前面に表示された Activity を取得する */
    val taskStackHotFlow = callbackFlow {
        val listener = object : ITaskStackListener.Stub() {
            override fun onTaskStackChanged() {
                // do nothing
            }

            override fun onActivityPinned(packageName: String?, userId: Int, taskId: Int, stackId: Int) {
                // do nothing
            }

            override fun onActivityUnpinned() {
                // do nothing
            }

            override fun onActivityRestartAttempt(task: ActivityManager.RunningTaskInfo?, homeTaskVisible: Boolean, clearedTask: Boolean, wasVisible: Boolean) {
                // do nothing
            }

            override fun onActivityForcedResizable(packageName: String?, taskId: Int, reason: Int) {
                // do nothing
            }

            override fun onActivityDismissingDockedTask() {
                // do nothing
            }

            override fun onActivityLaunchOnSecondaryDisplayFailed(taskInfo: ActivityManager.RunningTaskInfo?, requestedDisplayId: Int) {
                // do nothing
            }

            override fun onActivityLaunchOnSecondaryDisplayRerouted(taskInfo: ActivityManager.RunningTaskInfo?, requestedDisplayId: Int) {
                // do nothing
            }

            override fun onTaskCreated(taskId: Int, componentName: ComponentName?) {
                // do nothing
            }

            override fun onTaskRemoved(taskId: Int) {
                // do nothing
            }

            override fun onTaskMovedToFront(taskInfo: ActivityManager.RunningTaskInfo?) {
                trySend(taskInfo)
            }

            override fun onTaskDescriptionChanged(taskInfo: ActivityManager.RunningTaskInfo?) {
                // do nothing
            }

            override fun onActivityRequestedOrientationChanged(taskId: Int, requestedOrientation: Int) {
                // do nothing
            }

            override fun onTaskRemovalStarted(taskInfo: ActivityManager.RunningTaskInfo?) {
                // do nothing
            }

            override fun onTaskProfileLocked(taskInfo: ActivityManager.RunningTaskInfo?, userId: Int) {
                // do nothing
            }

            override fun onTaskSnapshotChanged(taskId: Int, snapshot: TaskSnapshot?) {
                // do nothing
            }

            override fun onTaskSnapshotInvalidated(taskId: Int) {
                // do nothing
            }

            override fun onBackPressedOnTaskRoot(taskInfo: ActivityManager.RunningTaskInfo?) {
                // do nothing
            }

            override fun onTaskDisplayChanged(taskId: Int, newDisplayId: Int) {
                // do nothing
            }

            override fun onRecentTaskListUpdated() {
                // do nothing
            }

            override fun onRecentTaskListFrozenChanged(frozen: Boolean) {
                // do nothing
            }

            override fun onRecentTaskRemovedForAddTask(taskId: Int) {
                // do nothing
            }

            override fun onTaskFocusChanged(taskId: Int, focused: Boolean) {
                // do nothing
            }

            override fun onTaskRequestedOrientationChanged(taskId: Int, requestedOrientation: Int) {
                // do nothing
            }

            override fun onActivityRotation(displayId: Int) {
                // do nothing
            }

            override fun onTaskMovedToBack(taskInfo: ActivityManager.RunningTaskInfo?) {
                // do nothing
            }

            override fun onLockTaskModeChanged(mode: Int) {
                // do nothing
            }
        }

        ShizukuServiceTool.activity.registerTaskStackListener(listener)
        awaitClose { ShizukuServiceTool.activity.unregisterTaskStackListener(listener) }
    }.stateIn(scope, kotlinx.coroutines.flow.SharingStarted.Eagerly, null)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()

        scope.launch {
            RegisterAppListTool.realtimeReadApplicationIdList(this@OnStop2FinishAndRemoveTaskService).collectLatest { idList ->
                while (isActive) {
                    // 削除対象が来るまで待つ
                    val removeTask = taskStackHotFlow.first { info -> info?.topActivity?.packageName in idList }
                    // 別のアプリが開かれるのを待つ
                    taskStackHotFlow.first { info -> info?.topActivity?.packageName !in idList }

                    if (removeTask != null) {
                        // 削除する
                        ShizukuServiceTool.activity.removeTask(removeTask.taskId)

                        // Shizuku 権限で Toast を出す
                        // Suppressing toast from package .... エラーで正規ルートでは表示できない
                        ShizukuServiceTool.notification.enqueueTextToast(
                            "com.android.shell",
                            Binder(),
                            getString(
                                // 変更
                                R.string.service_onstop_2_finish_and_remove_task_task_removed_toast_message_format,
                                removeTask.topActivityInfo?.loadLabel(packageManager)
                            ),
                            //"[${removeTask.topActivityInfo?.loadLabel(packageManager)}] タスクを削除しました",
                            Toast.LENGTH_SHORT,
                            isUiContext,
                            displayId,
                            object : ITransientNotificationCallback.Stub() {
                                override fun onToastShown() {
                                    // do nothing
                                }

                                override fun onToastHidden() {
                                    // do nothing
                                }
                            }
                        )
                    }
                }
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    private fun startForegroundService() {
        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            val notificationChannel = NotificationChannelCompat.Builder(CHANNEL_ID, NotificationManager.IMPORTANCE_LOW).apply {
                setName(getString(R.string.service_onstop_2_finish_and_remove_task_notification_channel_name))
            }.build()
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notification = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setContentTitle(getString(R.string.service_onstop_2_finish_and_remove_task_running_notification_title))
            setContentText(getString(R.string.service_onstop_2_finish_and_remove_task_running_notification_text))
            setSmallIcon(R.drawable.ic_launcher_foreground)
        }.build()
        startForeground(1, notification)
    }

    companion object {
        private const val CHANNEL_ID = "running_notification_channel_id"
    }
}
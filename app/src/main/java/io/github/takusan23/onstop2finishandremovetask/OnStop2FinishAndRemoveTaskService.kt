package io.github.takusan23.onstop2finishandremovetask

import android.app.ActivityManager
import android.app.ITaskStackListener
import android.app.NotificationManager
import android.app.Service
import android.content.ComponentName
import android.content.Intent
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


class OnStop2FinishAndRemoveTaskService : Service() {

    private val scope = MainScope()

    private val notificationManager by lazy { NotificationManagerCompat.from(this) }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()

        scope.launch {
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
            }.stateIn(scope)


            /*
                        val a = ShizukuServiceTool.activity.getRecentTasks(100, ActivityManager.RECENT_WITH_EXCLUDED and ActivityManager.RECENT_IGNORE_UNAVAILABLE, this@OnStop2FinishAndRemoveTaskService.userId)
                            .list
                            .filterIsInstance<ActivityManager.RecentTaskInfo>()
            */

            RegisterAppListTool.realtimeReadApplicationIdList(this@OnStop2FinishAndRemoveTaskService).collectLatest { idList ->
                while (isActive) {
                    // 削除対象が来るまで待つ
                    val removeTargetTask = taskStackHotFlow.first { info -> info?.topActivity?.packageName in idList }
                    // 別のアプリが開かれるのを待つ
                    taskStackHotFlow.first { info -> info?.topActivity?.packageName !in idList }
                    // 削除する
                    if (removeTargetTask != null) {
                        ShizukuServiceTool.activity.removeTask(removeTargetTask.taskId)
                        Toast.makeText(this@OnStop2FinishAndRemoveTaskService, "削除しました", Toast.LENGTH_SHORT).show()
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
                setName("実行中通知")
            }.build()
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notification = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setContentTitle("OnStop2FinishAndRemoveTask 実行中通知")
            setContentText("動作中です")
            setSmallIcon(R.drawable.ic_launcher_foreground)
        }.build()
        startForeground(1, notification)
    }

    companion object {
        private const val CHANNEL_ID = "running_notification_channel_id"
    }
}
package io.github.takusan23.onstop2finishandremovetask.ui.screen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.takusan23.onstop2finishandremovetask.tool.RegisterAppListTool
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AppListViewModel(private val application: Application) : AndroidViewModel(application) {
    private val context get() = application.applicationContext

    /**
     * アプリ一覧と登録状況の Flow
     */
    val appListFlow = combine(
        flow = flowOf(context.packageManager.getInstalledPackages(0)),
        flow2 = RegisterAppListTool.realtimeReadApplicationIdList(context),
        transform = ::Pair
    ).map { (appList, registerAppList) ->
        appList.mapNotNull { info ->
            AppInfo(
                isRegistered = info.packageName in registerAppList,
                appName = info.applicationInfo?.loadLabel(context.packageManager).toString(),
                packageName = info.packageName,
                versionName = info.versionName
            )
        }.sortedBy { it.packageName }
    }

    /**
     * アプリを登録する
     *
     * @param isRegistered 登録するなら true
     * @param packageName パッケージ名
     */
    fun registerApp(isRegistered: Boolean, packageName: String) {
        viewModelScope.launch {
            val currentAppList = RegisterAppListTool.readApplicationIdList(context)
            RegisterAppListTool.saveApplicationIdList(
                context = context,
                applicationIdList = if (isRegistered) {
                    currentAppList + packageName
                } else {
                    currentAppList - packageName
                }
            )
        }
    }

    data class AppInfo(
        val isRegistered: Boolean,
        val appName: String,
        val packageName: String,
        val versionName: String?
    )

}

package io.github.takusan23.onstop2finishandremovetask.ui.screen

import android.app.Application
import android.content.pm.ApplicationInfo
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.takusan23.onstop2finishandremovetask.tool.RegisterAppListTool
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppListViewModel(private val application: Application) : AndroidViewModel(application) {
    private val context get() = application.applicationContext
    private val _searchOptionFlow = MutableStateFlow(SearchOption())

    /**
     * アプリ一覧と登録状況の Flow
     */
    val appListFlow = combine(
        flow = flowOf(context.packageManager.getInstalledPackages(0)),
        flow2 = RegisterAppListTool.realtimeReadApplicationIdList(context),
        flow3 = _searchOptionFlow,
        transform = ::Triple
    ).map { (appList, registerAppList, searchOption) ->
        appList
            .mapNotNull { info ->
                AppInfo(
                    isRegistered = info.packageName in registerAppList,
                    isSystemApp = info.applicationInfo?.let { (it.flags and ApplicationInfo.FLAG_SYSTEM) != 0 } == true,
                    appName = info.applicationInfo?.loadLabel(context.packageManager).toString(),
                    packageName = info.packageName,
                    versionName = info.versionName
                )
            }
            .filter {
                val hasAppName = it.appName.contains(searchOption.searchAppName, ignoreCase = true)
                val hasPackageName = it.packageName.contains(searchOption.searchPackageName, ignoreCase = true)
                val hasSystemAppOrTrue = if (!searchOption.isIncludeSystemApp) !it.isSystemApp else true
                val hasOnlyRegisterAppOrTrue = if (searchOption.isOnlyRegisterApp) it.isRegistered else true
                hasAppName && hasPackageName && hasSystemAppOrTrue && hasOnlyRegisterAppOrTrue
            }
            .sortedBy { it.packageName }
    }

    /**
     * アプリの検索
     */
    val searchOptionFlow = _searchOptionFlow.asStateFlow()

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

    /**
     * 検索を変更する
     */
    fun updateSearchOption(searchOption: SearchOption) {
        _searchOptionFlow.value = searchOption
    }

    data class AppInfo(
        val isRegistered: Boolean,
        val isSystemApp: Boolean,
        val appName: String,
        val packageName: String,
        val versionName: String?
    )

    data class SearchOption(
        val searchAppName: String = "",
        val searchPackageName: String = "",
        val isIncludeSystemApp: Boolean = true,
        val isOnlyRegisterApp: Boolean = false
    ) {
        companion object {
            val EMPTY = SearchOption()
        }
    }
}

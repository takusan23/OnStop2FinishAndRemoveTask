package io.github.takusan23.onstop2finishandremovetask.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import io.github.takusan23.onstop2finishandremovetask.tool.ShizukuServiceTool

@Composable
fun MainScreen() {

    // Shizuku がバインドされてから権限チェック
    val backStack = remember { mutableStateListOf(RoutePaths.ShizukuPrepare) }
    LaunchedEffect(Unit) {
        backStack += if (ShizukuServiceTool.checkShizukuPermission()) {
            RoutePaths.AppList
        } else {
            RoutePaths.ShizukuSetup
        }
        backStack -= RoutePaths.ShizukuPrepare
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                RoutePaths.ShizukuPrepare -> NavEntry(RoutePaths.ShizukuPrepare) {
                    ShizukuPrepareScreen()
                }

                RoutePaths.ShizukuSetup -> NavEntry(RoutePaths.ShizukuSetup) {
                    ShizukuSetupScreen(onGranted = {
                        // 前の画面に戻れなくする
                        backStack += RoutePaths.AppList
                        backStack -= RoutePaths.ShizukuSetup
                    })
                }

                RoutePaths.AppList -> NavEntry(RoutePaths.AppList) {
                    AppListScreen(
                        viewModel = viewModel(),
                        onNavigate = { backStack += it }
                    )
                }

                RoutePaths.Setting -> NavEntry(RoutePaths.Setting) {
                    SettingScreen(onNavigation = { backStack += it })
                }

                RoutePaths.License -> NavEntry(RoutePaths.License) {
                    LicenseScreen()
                }
            }
        }
    )
}
package io.github.takusan23.onstop2finishandremovetask.ui.screen

import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import rikka.shizuku.Shizuku

@Composable
fun MainScreen() {
    val backStack = remember {
        mutableStateListOf(
            if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                RoutePaths.AppList
            } else {
                RoutePaths.ShizukuSetup
            }
        )
    }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                RoutePaths.ShizukuSetup -> NavEntry(RoutePaths.ShizukuSetup) {
                    ShizukuSetupScreen(onGranted = {
                        // 前の画面に戻れなくする
                        backStack.add(RoutePaths.AppList)
                    })
                }

                RoutePaths.AppList -> NavEntry(RoutePaths.AppList) {
                    AppListScreen(viewModel = viewModel())
                }
            }
        }
    )
}
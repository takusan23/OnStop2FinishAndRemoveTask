package io.github.takusan23.onstop2finishandremovetask.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppListScreen(viewModel: AppListViewModel) {
    val appList = viewModel.appListFlow.collectAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("有効にするアプリ選択") })
        }
    ) { paddingValues ->
        LazyColumn(contentPadding = paddingValues) {
            items(appList.value) { info ->
                AppListItem(
                    info = info,
                    onCheck = { isRegistered, packageName -> viewModel.registerApp(isRegistered, packageName) }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun AppListItem(
    modifier: Modifier = Modifier,
    info: AppListViewModel.AppInfo,
    onCheck: (Boolean, String) -> Unit
) {
    val context = LocalContext.current
    val bitmap = remember { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(key1 = Unit) {
        bitmap.value = context.packageManager.getApplicationIcon(info.packageName).toBitmap().asImageBitmap()
    }

    Row(
        modifier = modifier.padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        if (bitmap.value != null) {
            Image(
                modifier = Modifier.size(40.dp),
                bitmap = bitmap.value!!,
                contentDescription = null
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = info.appName,
                fontSize = 20.sp
            )
            Text(text = info.packageName)
        }
        Switch(
            checked = info.isRegistered,
            onCheckedChange = { onCheck(it, info.packageName) }
        )
    }
}
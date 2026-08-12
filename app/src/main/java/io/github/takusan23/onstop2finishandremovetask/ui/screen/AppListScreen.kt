package io.github.takusan23.onstop2finishandremovetask.ui.screen

import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import io.github.takusan23.onstop2finishandremovetask.OnStop2FinishAndRemoveTaskService
import io.github.takusan23.onstop2finishandremovetask.R

@Composable
fun AppListScreen(
    viewModel: AppListViewModel,
    onNavigate: (RoutePaths) -> Unit
) {
    val appList = viewModel.appListFlow.collectAsState(emptyList())
    val showSearchBottomSheet = remember { mutableStateOf(false) }
    val searchOption = viewModel.searchOptionFlow.collectAsState()

    if (showSearchBottomSheet.value) {
        SearchBottomSheet(
            onClose = { showSearchBottomSheet.value = false },
            searchOption = searchOption.value,
            onUpdate = { viewModel.updateSearchOption(it) }
        )
    }

    Scaffold(
        topBar = {
            TopAppBarWithSearchIcon(
                currentSearch = searchOption != AppListViewModel.SearchOption.EMPTY,
                onSearchClick = { showSearchBottomSheet.value = true },
                onSettingClick = { onNavigate(RoutePaths.Setting) }
            )
        }
    ) { paddingValues ->
        LazyColumn(contentPadding = paddingValues) {
            item {
                // 通知権限があればサービスを停めることが出来ます通知
                NotificationPermissionCard()
            }
            items(appList.value, key = { it.packageName }) { info ->
                AppListItem(
                    info = info,
                    onCheck = { isRegistered, packageName -> viewModel.registerApp(isRegistered, packageName) }
                )
                HorizontalDivider()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBottomSheet(
    onClose: () -> Unit,
    searchOption: AppListViewModel.SearchOption,
    onUpdate: (AppListViewModel.SearchOption) -> Unit
) {

    fun update(
        searchAppName: String = searchOption.searchAppName,
        searchPackageName: String = searchOption.searchPackageName,
        isIncludeSystemApp: Boolean = searchOption.isIncludeSystemApp,
        isOnlyRegisterApp: Boolean = searchOption.isOnlyRegisterApp
    ) {
        onUpdate(
            searchOption.copy(
                searchAppName = searchAppName,
                searchPackageName = searchPackageName,
                isIncludeSystemApp = isIncludeSystemApp,
                isOnlyRegisterApp = isOnlyRegisterApp
            )
        )
    }

    @Composable
    fun SwitchWithLabel(
        modifier: Modifier = Modifier,
        label: String,
        value: Boolean,
        onChange: (Boolean) -> Unit
    ) {
        Row(
            modifier = modifier
                .toggleable(value = value, onValueChange = onChange)
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = label,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = value,
                onCheckedChange = null
            )
        }
    }

    ModalBottomSheet(onDismissRequest = onClose) {
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(
                modifier = Modifier.padding(horizontal = 10.dp),
                text = stringResource(R.string.screen_app_list_search_bottomsheet_title),
                fontSize = 20.sp
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                value = searchOption.searchAppName,
                onValueChange = { update(searchAppName = it) },
                label = { Text(stringResource(R.string.screen_app_list_search_bottomsheet_search_app_name)) }
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                value = searchOption.searchPackageName,
                onValueChange = { update(searchPackageName = it) },
                label = { Text(stringResource(R.string.screen_app_list_search_bottomsheet_search_package_name)) }
            )
            SwitchWithLabel(
                label = stringResource(R.string.screen_app_list_search_bottomsheet_include_system_app),
                value = searchOption.isIncludeSystemApp,
                onChange = { update(isIncludeSystemApp = it) }
            )
            SwitchWithLabel(
                label = stringResource(R.string.screen_app_list_search_bottomsheet_only_register_app),
                value = searchOption.isOnlyRegisterApp,
                onChange = { update(isOnlyRegisterApp = it) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBarWithSearchIcon(
    currentSearch: Boolean,
    onSearchClick: () -> Unit,
    onSettingClick: () -> Unit
) {
    TopAppBar(
        title = { Text(stringResource(R.string.screen_app_list_top_app_bar_title)) },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    painter = painterResource(R.drawable.search_24px),
                    contentDescription = null,
                    tint = if (currentSearch) MaterialTheme.colorScheme.primary else LocalContentColor.current
                )
            }
            IconButton(onClick = onSettingClick) {
                Icon(
                    painter = painterResource(R.drawable.settings_24px),
                    contentDescription = null
                )
            }
        }
    )
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
        modifier = modifier
            .toggleable(
                value = info.isRegistered,
                onValueChange = { onCheck(it, info.packageName) }
            )
            .padding(10.dp),
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

@Composable
private fun NotificationPermissionCard(modifier: Modifier = Modifier) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    val context = LocalContext.current
    val isGranted = remember { mutableStateOf(ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) }
    val permissionRequester = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
        if (result) {
            // 再起動しておく...
            isGranted.value = true
            OnStop2FinishAndRemoveTaskService.stop(context)
            OnStop2FinishAndRemoveTaskService.start(context)
        }
    }

    if (isGranted.value) return

    OutlinedCard(modifier = modifier.padding(5.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
        ) {
            Text(stringResource(R.string.screen_app_list_permission_card_title))
            Button(onClick = { permissionRequester.launch(android.Manifest.permission.POST_NOTIFICATIONS) }) {
                Text(stringResource(R.string.screen_app_list_permission_card_button))
            }
        }
    }
}
package io.github.takusan23.onstop2finishandremovetask.ui.screen

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import io.github.takusan23.onstop2finishandremovetask.R

private const val ShizukuWebSite = "https://shizuku.rikka.app/"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShizukuPrepareScreen() {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = stringResource(R.string.screen_shizuku_prepare_title)) })
        }
    ) { innerPadding ->
        OutlinedCard(
            modifier = Modifier
                .padding(innerPadding)
                .padding(10.dp)
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                CircularProgressIndicator()
                Text(
                    text = stringResource(R.string.screen_shizuku_prepare_description_title),
                    fontSize = 20.sp
                )
                Text(text = stringResource(R.string.screen_shizuku_prepare_description_body))
                Button(onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, ShizukuWebSite.toUri())) }) {
                    Text(text = stringResource(R.string.screen_shizuku_prepare_button))
                }
            }
        }
    }
}
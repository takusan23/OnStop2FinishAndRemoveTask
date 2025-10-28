package io.github.takusan23.onstop2finishandremovetask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.takusan23.onstop2finishandremovetask.ui.screen.MainScreen
import io.github.takusan23.onstop2finishandremovetask.ui.theme.OnStop2FinishAndRemoveTaskTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OnStop2FinishAndRemoveTaskTheme {
                MainScreen()
            }
        }
    }
}
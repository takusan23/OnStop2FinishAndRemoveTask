package io.github.takusan23.onstop2finishandremovetask

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import io.github.takusan23.onstop2finishandremovetask.ui.screen.MainScreen
import io.github.takusan23.onstop2finishandremovetask.ui.theme.OnStop2FinishAndRemoveTaskTheme
import org.lsposed.hiddenapibypass.HiddenApiBypass

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        HiddenApiBypass.addHiddenApiExemptions("")

        setContent {
            OnStop2FinishAndRemoveTaskTheme {
                MainScreen()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Foreground Service 開始
        ContextCompat.startForegroundService(this, Intent(this, OnStop2FinishAndRemoveTaskService::class.java))
    }

}
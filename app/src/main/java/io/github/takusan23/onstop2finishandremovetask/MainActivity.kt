package io.github.takusan23.onstop2finishandremovetask

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import io.github.takusan23.onstop2finishandremovetask.ui.screen.MainScreen
import io.github.takusan23.onstop2finishandremovetask.ui.theme.OnStop2FinishAndRemoveTaskTheme
import kotlinx.coroutines.launch
import org.lsposed.hiddenapibypass.HiddenApiBypass
import rikka.shizuku.Shizuku
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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
        lifecycleScope.launch {

            // Shizuku の権限付与まで待つ
            if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
                suspendCoroutine { continuation ->
                    Shizuku.addRequestPermissionResultListener { _, grantResult ->
                        if (grantResult == PackageManager.PERMISSION_GRANTED) {
                            continuation.resume(Unit)
                        }
                    }
                }
            }

            // Foreground Service 開始
            ContextCompat.startForegroundService(this@MainActivity, Intent(this@MainActivity, OnStop2FinishAndRemoveTaskService::class.java))
        }
    }

}
package com.seriesly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.seriesly.core.security.session.SessionManager
import com.seriesly.core.ui.theme.SerieslyTheme
import com.seriesly.navigation.SerieslyNavHost
import com.seriesly.sync.SyncWorker
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
        )
        val startDestination = if (sessionManager.isLoggedIn()) "home" else "auth"
        setContent {
            SerieslyTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SerieslyNavHost(startDestination = startDestination)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (sessionManager.isLoggedIn()) {
            SyncWorker.enqueue(this)
        }
    }
}

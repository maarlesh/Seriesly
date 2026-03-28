package com.seriesly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.seriesly.core.security.session.SessionManager
import com.seriesly.core.ui.theme.SerieslyTheme
import com.seriesly.navigation.SerieslyNavHost
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val startDestination = if (sessionManager.isLoggedIn()) "home" else "auth"
        setContent {
            SerieslyTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SerieslyNavHost(startDestination = startDestination)
                }
            }
        }
    }
}

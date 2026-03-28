package com.seriesly.feature.auth.presentation.login

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seriesly.core.ui.theme.BrandPurple
import com.seriesly.core.ui.theme.BrandPurpleDim

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                LoginEvent.NavigateToHome     -> onNavigateToHome()
                LoginEvent.NavigateToRegister -> onNavigateToRegister()
            }
        }
    }

    // Drive sequential entry animations
    var launched by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { launched = true }

    val logoScale by animateFloatAsState(
        targetValue   = if (launched) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
        label         = "logoScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Radial gradient background glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            BrandPurpleDim.copy(alpha = 0.25f),
                            Color.Transparent
                        ),
                        center = Offset(0.5f, 0f),
                        radius = 800f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .scale(logoScale)
                    .clip(MaterialTheme.shapes.extraLarge)
                    .background(BrandPurple),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Filled.Tv,
                    contentDescription = null,
                    tint               = Color.White,
                    modifier           = Modifier.size(36.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            // App title
            AnimatedVisibility(
                visible = launched,
                enter   = slideInVertically(tween(300, delayMillis = 100)) { 20 } + fadeIn(tween(300, delayMillis = 100))
            ) {
                Text("Seriesly", style = MaterialTheme.typography.headlineLarge)
            }

            AnimatedVisibility(
                visible = launched,
                enter   = slideInVertically(tween(300, delayMillis = 150)) { 20 } + fadeIn(tween(300, delayMillis = 150))
            ) {
                Text(
                    "Track what you love, always.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(40.dp))

            // Username field
            AnimatedVisibility(
                visible = launched,
                enter   = slideInVertically(tween(300, delayMillis = 200)) { 20 } + fadeIn(tween(300, delayMillis = 200))
            ) {
                OutlinedTextField(
                    value         = uiState.username,
                    onValueChange = { viewModel.onIntent(LoginIntent.UsernameChanged(it)) },
                    label         = { Text("Username") },
                    singleLine    = true,
                    shape         = RoundedCornerShape(14.dp),
                    modifier      = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(12.dp))

            // Password field
            AnimatedVisibility(
                visible = launched,
                enter   = slideInVertically(tween(300, delayMillis = 230)) { 20 } + fadeIn(tween(300, delayMillis = 230))
            ) {
                OutlinedTextField(
                    value               = uiState.password,
                    onValueChange       = { viewModel.onIntent(LoginIntent.PasswordChanged(it)) },
                    label               = { Text("Password") },
                    singleLine          = true,
                    shape               = RoundedCornerShape(14.dp),
                    visualTransformation = if (uiState.passwordVisible) VisualTransformation.None
                                          else PasswordVisualTransformation(),
                    trailingIcon        = {
                        IconButton(onClick = { viewModel.onIntent(LoginIntent.TogglePasswordVisible) }) {
                            Icon(
                                if (uiState.passwordVisible) Icons.Outlined.VisibilityOff
                                else Icons.Outlined.Visibility,
                                contentDescription = "Toggle password visibility"
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (uiState.error != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(Modifier.height(24.dp))

            // Sign In button
            AnimatedVisibility(
                visible = launched,
                enter   = slideInVertically(tween(300, delayMillis = 270)) { 20 } + fadeIn(tween(300, delayMillis = 270))
            ) {
                Button(
                    onClick  = { viewModel.onIntent(LoginIntent.LoginClicked) },
                    enabled  = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    AnimatedContent(targetState = uiState.isLoading, label = "loginBtn") { loading ->
                        if (loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color    = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Sign In →", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            AnimatedVisibility(
                visible = launched,
                enter   = fadeIn(tween(300, delayMillis = 320))
            ) {
                TextButton(onClick = { viewModel.onIntent(LoginIntent.NavigateToRegister) }) {
                    Text("Don't have an account? Create one")
                }
            }
        }
    }
}

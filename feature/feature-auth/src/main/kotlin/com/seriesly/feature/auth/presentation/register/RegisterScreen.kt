package com.seriesly.feature.auth.presentation.register

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seriesly.core.ui.theme.*
import com.seriesly.core.ui.tokens.Brushes
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                RegisterEvent.NavigateToHome  -> onNavigateToHome()
                RegisterEvent.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Background Blobs
        Box(modifier = Modifier.size(480.dp).offset(100.dp, (-100).dp).align(Alignment.TopEnd).background(Brush.radialGradient(listOf(Primary.copy(alpha = 0.1f), Color.Transparent))))
        Box(modifier = Modifier.size(320.dp).offset((-60).dp, 60.dp).align(Alignment.BottomStart).background(Brush.radialGradient(listOf(Secondary.copy(alpha = 0.05f), Color.Transparent))))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding() // Placed here to shrink the actual viewport
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // This spacer pushes content to the center when there's room,
            // but disappears when the keyboard takes up space
            Spacer(Modifier.weight(1f))
            Spacer(Modifier.height(32.dp))

            // Brand header
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(Brushes.SoulGradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Movie, null, tint = OnPrimaryContainer, modifier = Modifier.size(28.dp))
            }

            Spacer(Modifier.height(16.dp))

            Text(text = "Seriesly", style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold, fontStyle = FontStyle.Italic), color = Primary)

            Spacer(Modifier.height(32.dp))

            val passwordFocus = remember { FocusRequester() }
            val confirmFocus  = remember { FocusRequester() }

            // Glass panel card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfaceContainer.copy(alpha = 0.85f))
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text("Create Account", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = OnSurface, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())

                RegisterField(
                    label = "USERNAME",
                    value = uiState.username,
                    onValueChange = { viewModel.onIntent(RegisterIntent.UsernameChanged(it)) },
                    errorText = uiState.usernameError,
                    imeAction = ImeAction.Next,
                    onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
                )

                RegisterField(
                    label = "PASSWORD",
                    value = uiState.password,
                    onValueChange = { viewModel.onIntent(RegisterIntent.PasswordChanged(it)) },
                    errorText = uiState.passwordError,
                    visualTransformation = if (uiState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardType = KeyboardType.Password,
                    focusRequester = passwordFocus,
                    imeAction = ImeAction.Next,
                    onImeAction = { focusManager.moveFocus(FocusDirection.Down) },
                    trailingIcon = {
                        IconButton(onClick = { viewModel.onIntent(RegisterIntent.TogglePasswordVisible) }) {
                            Icon(if (uiState.passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility, null, tint = OnSurfaceVariant)
                        }
                    }
                )

                RegisterField(
                    label = "CONFIRM PASSWORD",
                    value = uiState.confirmPassword,
                    onValueChange = { viewModel.onIntent(RegisterIntent.ConfirmChanged(it)) },
                    errorText = uiState.confirmError,
                    visualTransformation = if (uiState.confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardType = KeyboardType.Password,
                    focusRequester = confirmFocus,
                    imeAction = ImeAction.Done,
                    onImeAction = { viewModel.onIntent(RegisterIntent.RegisterClicked) },
                    trailingIcon = {
                        IconButton(onClick = { viewModel.onIntent(RegisterIntent.ToggleConfirmVisible) }) {
                            Icon(if (uiState.confirmVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility, null, tint = OnSurfaceVariant)
                        }
                    }
                )

                RegisterSoulButton(text = "Create Account", isLoading = uiState.isLoading, onClick = { viewModel.onIntent(RegisterIntent.RegisterClicked) })
            }

            Spacer(Modifier.height(24.dp))
            Row {
                Text("Already have an account? ", color = OnSurfaceVariant)
                Text("Sign In", color = Primary, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onNavigateToLogin() })
            }

            // Bottom weight to keep things balanced
            Spacer(Modifier.weight(1.2f))
            Spacer(Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RegisterField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    errorText: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType = KeyboardType.Text,
    focusRequester: FocusRequester? = null,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    val scope = rememberCoroutineScope()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .bringIntoViewRequester(bringIntoViewRequester)
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = if (errorText != null) Error else OnSurfaceVariant)
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceContainerLow)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                visualTransformation = visualTransformation,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
                keyboardActions = KeyboardActions(onAny = { onImeAction?.invoke() }),
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = OnSurface),
                cursorBrush = SolidColor(Primary),
                modifier = Modifier
                    .weight(1f)
                    .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
                    .onFocusChanged {
                        if (it.isFocused) {
                            scope.launch {
                                delay(100) // Give the keyboard a moment to start opening
                                bringIntoViewRequester.bringIntoView()
                            }
                        }
                    }
            )
            trailingIcon?.invoke()
        }
    }
}

@Composable
private fun RegisterSoulButton(text: String, isLoading: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(12.dp)).background(Brushes.SoulGradient).clickable(enabled = !isLoading, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) CircularProgressIndicator(color = OnPrimaryContainer, modifier = Modifier.size(24.dp))
        else Text(text, color = OnPrimaryContainer, fontWeight = FontWeight.Bold)
    }
}
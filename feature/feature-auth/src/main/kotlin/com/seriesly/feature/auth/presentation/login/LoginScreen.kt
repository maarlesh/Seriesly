package com.seriesly.feature.auth.presentation.login

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Ambient glow — top-right primary blob
        Box(
            modifier = Modifier
                .size(480.dp)
                .offset(x = 100.dp, y = (-100).dp)
                .align(Alignment.TopEnd)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Primary.copy(alpha = 0.10f), Color.Transparent)
                    )
                )
        )
        // Ambient glow — bottom-left secondary blob
        Box(
            modifier = Modifier
                .size(320.dp)
                .offset(x = (-60).dp, y = 60.dp)
                .align(Alignment.BottomStart)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Secondary.copy(alpha = 0.05f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))

            // Brand header
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brushes.SoulGradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Filled.Movie,
                    contentDescription = null,
                    tint               = OnPrimaryContainer,
                    modifier           = Modifier.size(28.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text  = "Seriesly",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontStyle  = FontStyle.Italic
                ),
                color = Primary
            )
            Text(
                text  = "Enter the Cinematic Obsidian.",
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant
            )

            Spacer(Modifier.height(32.dp))

            val passwordFocus = remember { FocusRequester() }

            // Glass panel card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfaceContainer.copy(alpha = 0.85f))
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text      = "Sign In",
                    style     = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color     = OnSurface,
                    textAlign = TextAlign.Center,
                    modifier  = Modifier.fillMaxWidth()
                )

                CinematicField(
                    label         = "USERNAME",
                    value         = uiState.username,
                    onValueChange = { viewModel.onIntent(LoginIntent.UsernameChanged(it)) },
                    imeAction     = ImeAction.Next,
                    onImeAction   = { passwordFocus.requestFocus() },
                    leadingIcon   = {
                        Icon(Icons.Outlined.Person, null,
                            tint = OnSurfaceVariant, modifier = Modifier.size(20.dp))
                    }
                )

                CinematicField(
                    label               = "PASSWORD",
                    value               = uiState.password,
                    onValueChange       = { viewModel.onIntent(LoginIntent.PasswordChanged(it)) },
                    visualTransformation = if (uiState.passwordVisible) VisualTransformation.None
                                         else PasswordVisualTransformation(),
                    keyboardType        = KeyboardType.Password,
                    focusRequester      = passwordFocus,
                    imeAction           = ImeAction.Done,
                    onImeAction         = { viewModel.onIntent(LoginIntent.LoginClicked) },
                    leadingIcon = {
                        Icon(Icons.Outlined.Lock, null,
                            tint = OnSurfaceVariant, modifier = Modifier.size(20.dp))
                    },
                    trailingIcon = {
                        IconButton(
                            onClick  = { viewModel.onIntent(LoginIntent.TogglePasswordVisible) },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = if (uiState.passwordVisible) Icons.Outlined.VisibilityOff
                                              else Icons.Outlined.Visibility,
                                contentDescription = "Toggle password",
                                tint   = OnSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                )

                if (uiState.error != null) {
                    Text(
                        text  = uiState.error!!,
                        color = Error,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                SoulGradientButton(
                    text      = "Sign In",
                    isLoading = uiState.isLoading,
                    onClick   = { viewModel.onIntent(LoginIntent.LoginClicked) }
                )
            }

            Spacer(Modifier.height(20.dp))

            Row {
                Text(
                    "Don't have an account?  ",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant
                )
                Text(
                    "Create one",
                    style    = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color    = Primary,
                    modifier = Modifier.clickable { viewModel.onIntent(LoginIntent.NavigateToRegister) }
                )
            }

            Spacer(Modifier.height(48.dp))
        }
    }
}

// ── Shared Cinematic components (private to auth) ────────────────────────────

@Composable
private fun CinematicField(
    label:                String,
    value:                String,
    onValueChange:        (String) -> Unit,
    leadingIcon:          @Composable (() -> Unit)? = null,
    trailingIcon:         @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType:         KeyboardType = KeyboardType.Text,
    focusRequester:       FocusRequester? = null,
    imeAction:            ImeAction = ImeAction.Default,
    onImeAction:          (() -> Unit)? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text     = label,
            style    = MaterialTheme.typography.labelSmall,
            color    = OnSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceContainerLow)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            leadingIcon?.invoke()
            BasicTextField(
                value                = value,
                onValueChange        = onValueChange,
                singleLine           = true,
                visualTransformation = visualTransformation,
                keyboardOptions      = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
                keyboardActions      = KeyboardActions(
                    onNext = { onImeAction?.invoke() },
                    onDone = { onImeAction?.invoke() }
                ),
                textStyle            = MaterialTheme.typography.bodyMedium.copy(color = OnSurface),
                cursorBrush          = SolidColor(Primary),
                modifier             = Modifier
                    .weight(1f)
                    .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
            ) { innerField ->
                if (value.isEmpty()) {
                    Text(
                        text  = label.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Outline.copy(alpha = 0.5f)
                    )
                }
                innerField()
            }
            trailingIcon?.invoke()
        }
    }
}

@Composable
private fun SoulGradientButton(
    text:      String,
    isLoading: Boolean,
    onClick:   () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue   = if (isPressed) 0.98f else 1f,
        label         = "btnScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(Brushes.SoulGradient)
            .clickable(
                interactionSource = interactionSource,
                indication        = null,
                enabled           = !isLoading,
                onClick           = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(targetState = isLoading, label = "btnContent") { loading ->
            if (loading) {
                CircularProgressIndicator(
                    modifier    = Modifier.size(22.dp),
                    color       = OnPrimaryContainer,
                    strokeWidth = 2.5.dp
                )
            } else {
                Text(
                    text  = text,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp
                    ),
                    color = OnPrimaryContainer
                )
            }
        }
    }
}

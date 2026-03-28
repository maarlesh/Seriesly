package com.seriesly.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.seriesly.core.ui.tokens.Spacing
import kotlinx.coroutines.delay

private const val MAX_COMMENT_LENGTH = 500

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingBottomSheet(
    title: String,
    initialRating: Float = 0f,
    initialComment: String = "",
    onDismiss: () -> Unit,
    onSave: (rating: Float, comment: String?) -> Unit
) {
    var rating        by remember { mutableFloatStateOf(initialRating) }
    var displayRating by remember { mutableFloatStateOf(if (initialRating > 0f) 0f else 0f) }
    var comment       by remember { mutableStateOf(initialComment) }

    // Sequentially fill stars one-by-one when sheet opens with an existing rating
    LaunchedEffect(Unit) {
        if (initialRating > 0f) {
            for (i in 1..5) {
                delay(60L * i)
                displayRating = minOf(initialRating, i.toFloat())
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = Spacing.md)
                .padding(bottom = Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text  = "Rate \u201c$title\u201d",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(Spacing.lg))

            StarRatingInput(
                currentRating   = displayRating,
                onRatingChanged = {
                    rating        = it
                    displayRating = it
                }
            )

            if (displayRating > 0f) {
                Spacer(Modifier.height(Spacing.xs))
                Text(
                    text  = "$displayRating / 5",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(Spacing.md))

            OutlinedTextField(
                value         = comment,
                onValueChange = { if (it.length <= MAX_COMMENT_LENGTH) comment = it },
                label         = { Text("Add a comment (optional)") },
                maxLines      = 4,
                modifier      = Modifier.fillMaxWidth(),
                supportingText = {
                    Text(
                        text  = "${comment.length} / $MAX_COMMENT_LENGTH",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            )

            Spacer(Modifier.height(Spacing.lg))

            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick  = onDismiss,
                    modifier = Modifier.weight(1f)
                ) { Text("Skip") }

                Button(
                    onClick  = { if (rating > 0f) onSave(rating, comment.ifBlank { null }) },
                    enabled  = rating > 0f,
                    modifier = Modifier.weight(1f)
                ) { Text("Save Rating") }
            }
        }
    }
}

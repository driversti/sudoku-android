package live.yurii.sudoku.presentation.screen.game

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import live.yurii.sudoku.domain.model.Game

@Composable
fun GameCompletedDialog(
    game: Game,
    onNewGame: () -> Unit,
    onBack: () -> Unit,
    onDismiss: () -> Unit,
    onSubmitScore: () -> Unit = {},
    isLoggedIn: Boolean = false,
    isSubmittingScore: Boolean = false
) {
    Dialog(onDismissRequest = onDismiss) {
        AnimatedContent(
            targetState = isSubmittingScore,
            transitionSpec = {
                fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMedium)) togetherWith
                        fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMedium))
            },
            label = "dialog_animation"
        ) { submitting ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Trophy Icon
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Victory",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = "Puzzle Completed!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Stats
                StatRow(label = "Difficulty", value = game.difficulty.displayName)
                StatRow(label = "Time", value = formatTime(game.elapsedTime))
                StatRow(label = "Hints Used", value = game.hintsUsed.toString())
                StatRow(label = "Moves", value = game.currentMoves.toString())

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
                ) {
                    if (isLoggedIn) {
                        // Submit Score button (only if logged in)
                        Button(
                            onClick = onSubmitScore,
                            enabled = !isSubmittingScore,
                            modifier = Modifier.size(width = 100.dp, height = 48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary
                            )
                        ) {
                            if (isSubmittingScore) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onTertiary
                                )
                            } else {
                                Text("Submit", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    } else {
                        // Login placeholder (if not logged in)
                        Button(
                            onClick = { /* Navigate to login */ },
                            enabled = false,
                            modifier = Modifier.size(width = 100.dp, height = 48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text("Login", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Button(
                        onClick = onBack,
                        modifier = Modifier.size(width = 100.dp, height = 48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Back", style = MaterialTheme.typography.labelSmall)
                    }

                    Button(
                        onClick = onNewGame,
                        modifier = Modifier.size(width = 100.dp, height = 48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("New Game", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}

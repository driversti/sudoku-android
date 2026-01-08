package live.yurii.sudoku.presentation.screen.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import live.yurii.sudoku.domain.model.Difficulty

@Composable
fun WelcomeScreen(
    onStartGame: (Difficulty) -> Unit,
    onLeaderboardClick: () -> Unit = {},
    onStatsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title
            Text(
                text = "Sudoku",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Challenge your mind with the classic logic puzzle",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Difficulty Selection
            Text(
                text = "Select Difficulty",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Difficulty Buttons
            DifficultyButtons(onStartGame = onStartGame)

            Spacer(modifier = Modifier.height(32.dp))

            // Quick Actions
            RowOfActions(
                onLeaderboardClick = onLeaderboardClick,
                onStatsClick = onStatsClick,
                onSettingsClick = onSettingsClick,
                onProfileClick = onProfileClick
            )
        }
    }
}

@Composable
private fun RowOfActions(
    onLeaderboardClick: () -> Unit,
    onStatsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Leaderboard
        IconButton(
            onClick = onLeaderboardClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = "Leaderboard",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }

        // Statistics
        IconButton(
            onClick = onStatsClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.BarChart,
                contentDescription = "Statistics",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }

        // Settings
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }

        // Profile/Login
        IconButton(
            onClick = onProfileClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun DifficultyButtons(onStartGame: (Difficulty) -> Unit) {
    Column(
        modifier = Modifier.width(200.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DifficultyButton(
            text = "Easy",
            color = androidx.compose.ui.graphics.Color(0xFF4CAF50),
            onClick = { onStartGame(Difficulty.EASY) }
        )
        DifficultyButton(
            text = "Medium",
            color = androidx.compose.ui.graphics.Color(0xFF2196F3),
            onClick = { onStartGame(Difficulty.MEDIUM) }
        )
        DifficultyButton(
            text = "Hard",
            color = androidx.compose.ui.graphics.Color(0xFFFF9800),
            onClick = { onStartGame(Difficulty.HARD) }
        )
        DifficultyButton(
            text = "Expert",
            color = androidx.compose.ui.graphics.Color(0xFFF44336),
            onClick = { onStartGame(Difficulty.EXPERT) }
        )
    }
}

@Composable
private fun DifficultyButton(
    text: String,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = androidx.compose.ui.graphics.Color.White
        )
    }
}

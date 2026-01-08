package live.yurii.sudoku.presentation.screen.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    onBack: () -> Unit,
    viewModel: GameViewModel = hiltViewModel()
) {
    val gameState by viewModel.gameState.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val isSubmittingScore by viewModel.isSubmittingScore.collectAsState()
    val currentGame = (gameState as? GameState.Success)?.game

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentGame?.difficulty?.displayName ?: "Sudoku",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (currentGame != null) {
                // Timer
                Text(
                    text = formatTime(currentGame.elapsedTime),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Board
                SudokuBoard(
                    board = currentGame.currentBoard,
                    selectedCell = currentGame.selectedCell,
                    highlightedNumber = currentGame.highlightedNumber,
                    initialCells = currentGame.initialCells,
                    pencilMarks = currentGame.pencilMarks,
                    onCellClick = { position ->
                        viewModel.onCellClick(position.row, position.col)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Number Pad
                NumberPad(
                    onNumberClick = { number ->
                        viewModel.onNumberInput(number)
                    },
                    onClearClick = { viewModel.onClear() }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                ActionButtons(
                    canUndo = currentGame.canUndo,
                    canRedo = currentGame.canRedo,
                    pencilMarkMode = currentGame.pencilMarkMode,
                    onUndo = { viewModel.onUndo() },
                    onRedo = { viewModel.onRedo() },
                    onHint = { viewModel.onHint() },
                    onTogglePencilMark = { viewModel.onTogglePencilMarkMode() }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Loading...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }

    // Show completion dialog
    if (currentGame?.isComplete == true) {
        GameCompletedDialog(
            game = currentGame,
            onNewGame = {
                viewModel.startNewGame(currentGame.difficulty)
            },
            onBack = onBack,
            onDismiss = { /* Dialog can't be dismissed */ },
            onSubmitScore = { viewModel.onSubmitScore() },
            isLoggedIn = isLoggedIn,
            isSubmittingScore = isSubmittingScore
        )
    }
}

@Composable
fun SudokuBoard(
    board: live.yurii.sudoku.domain.model.Board,
    selectedCell: live.yurii.sudoku.domain.model.CellPosition?,
    highlightedNumber: Int?,
    initialCells: List<Boolean>,
    pencilMarks: List<Set<Int>>,
    onCellClick: (live.yurii.sudoku.domain.model.CellPosition) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                Color.LightGray.copy(alpha = 0.3f),
                RoundedCornerShape(8.dp)
            )
            .border(2.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
            .padding(4.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(9),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            items(81) { index ->
                val row = index / 9
                val col = index % 9
                val cell = board.getCell(live.yurii.sudoku.domain.model.CellPosition(row, col))
                val position = live.yurii.sudoku.domain.model.CellPosition(row, col)
                val isSelected = selectedCell?.row == row && selectedCell?.col == col
                val isHighlighted = highlightedNumber != null && cell?.value == highlightedNumber
                val isInitial = initialCells[index]
                val marks = pencilMarks[index]

                SudokuCell(
                    value = cell?.value,
                    pencilMarks = marks,
                    isSelected = isSelected,
                    isHighlighted = isHighlighted,
                    isInitial = isInitial,
                    row = row,
                    col = col,
                    onClick = { onCellClick(position) }
                )
            }
        }
    }
}

@Composable
fun SudokuCell(
    value: Int?,
    pencilMarks: Set<Int>,
    isSelected: Boolean,
    isHighlighted: Boolean,
    isInitial: Boolean,
    row: Int,
    col: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        isHighlighted -> MaterialTheme.colorScheme.secondaryContainer
        isInitial -> Color.Transparent
        else -> Color.White
    }

    val textColor = when {
        isInitial -> MaterialTheme.colorScheme.onBackground
        isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.primary
    }

    // Accessibility description for screen readers
    val cellDescription = when {
        value != null -> {
            val state = if (isInitial) "initial" else "filled"
            "Row ${row + 1}, column ${col + 1}, $state, contains $value"
        }
        pencilMarks.isNotEmpty() -> {
            val marksList = pencilMarks.sorted().joinToString(", ")
            "Row ${row + 1}, column ${col + 1}, empty, pencil marks: $marksList"
        }
        else -> "Row ${row + 1}, column ${col + 1}, empty"
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = cellDescription
                if (isSelected) heading()
            },
        contentAlignment = Alignment.Center
    ) {
        if (value != null) {
            Text(
                text = value.toString(),
                style = if (isInitial) {
                    MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                } else {
                    MaterialTheme.typography.bodyLarge
                },
                color = textColor,
                textAlign = TextAlign.Center
            )
        } else if (pencilMarks.isNotEmpty()) {
            // Display pencil marks in a 3x3 grid
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                for (row in 0..2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        for (col in 0..2) {
                            val num = row * 3 + col + 1
                            val text = if (num in pencilMarks) num.toString() else ""
                            Text(
                                text = text,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NumberPad(
    onNumberClick: (Int) -> Unit,
    onClearClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Numbers 1-9
        for (row in 0..2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                for (col in 0..2) {
                    val number = row * 3 + col + 1
                    NumberButton(
                        number = number,
                        onClick = { onNumberClick(number) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Clear button
        Button(
            onClick = onClearClick,
            modifier = Modifier.size(width = 120.dp, height = 48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("Clear", color = MaterialTheme.colorScheme.onSecondary)
        }
    }
}

@Composable
fun NumberButton(
    number: Int,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(56.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = number.toString(),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun ActionButtons(
    canUndo: Boolean,
    canRedo: Boolean,
    pencilMarkMode: Boolean,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onHint: () -> Unit,
    onTogglePencilMark: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
    ) {
        ActionButton(text = "Undo", enabled = canUndo, onClick = onUndo)
        ActionButton(text = "Redo", enabled = canRedo, onClick = onRedo)
        ActionButton(text = "Hint", enabled = true, onClick = onHint)

        // Pencil mark mode toggle
        OutlinedButton(
            onClick = onTogglePencilMark,
            modifier = Modifier.size(width = 80.dp, height = 48.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (pencilMarkMode) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    Color.Transparent
                }
            )
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Pencil Marks",
                tint = if (pencilMarkMode) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(width = 80.dp, height = 48.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelSmall)
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}

package live.yurii.sudoku.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Game(
    val id: String,
    val difficulty: Difficulty,
    val puzzle: Board,
    val solution: Board,
    val currentBoard: Board,
    val pencilMarks: List<Set<Int>>,
    val initialCells: List<Boolean>,
    val selectedCell: CellPosition?,
    val highlightedNumber: Int?,
    val isPaused: Boolean,
    val isComplete: Boolean,
    val hintsUsed: Int,
    val history: List<Move>,
    val historyIndex: Int,
    val startTime: Long,
    val elapsedTime: Long,
    val pencilMarkMode: Boolean = false
) {
    companion object {
        fun create(difficulty: Difficulty, puzzle: Board, solution: Board): Game {
            val initialCells = puzzle.cells.flatten().map { it != null }
            return Game(
                id = java.util.UUID.randomUUID().toString(),
                difficulty = difficulty,
                puzzle = puzzle,
                solution = solution,
                currentBoard = puzzle,
                pencilMarks = List(81) { emptySet() },
                initialCells = initialCells,
                selectedCell = null,
                highlightedNumber = null,
                isPaused = false,
                isComplete = false,
                hintsUsed = 0,
                history = emptyList(),
                historyIndex = -1,
                startTime = System.currentTimeMillis(),
                elapsedTime = 0
            )
        }
    }

    val currentMoves: Int
        get() = if (historyIndex >= 0) historyIndex + 1 else 0

    val canUndo: Boolean
        get() = historyIndex >= 0

    val canRedo: Boolean
        get() = historyIndex < history.size - 1

    fun isInitialCell(position: CellPosition): Boolean {
        return initialCells[position.toIndex()]
    }
}

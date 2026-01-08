package live.yurii.sudoku.domain.usecase.game

import live.yurii.sudoku.domain.model.CellPosition
import live.yurii.sudoku.domain.model.Game
import live.yurii.sudoku.domain.model.Move
import live.yurii.sudoku.domain.repository.IGameRepository
import live.yurii.sudoku.domain.util.SudokuGenerator
import javax.inject.Inject

class SetCellValueUseCase @Inject constructor(
    private val gameRepository: IGameRepository
) {
    suspend operator fun invoke(
        game: Game,
        position: CellPosition,
        value: Int?
    ): Result<Game> {
        if (game.isPaused || game.isComplete) {
            return Result.failure(IllegalStateException("Game is paused or complete"))
        }

        val index = position.toIndex()
        if (game.initialCells[index]) {
            return Result.failure(IllegalStateException("Cannot modify initial cell"))
        }

        val previousValue = game.currentBoard.getCell(position)?.value
        if (previousValue == value) {
            return Result.success(game)
        }

        // Clear pencil marks for this cell
        val newPencilMarks = game.pencilMarks.toMutableList().apply {
            this[index] = emptySet()
        }

        // Create new move
        val move = Move(
            position = position,
            value = value,
            previousValue = previousValue,
            timestamp = System.currentTimeMillis()
        )

        // Update board
        val newBoard = game.currentBoard.updateCell(position, value)

        // Trim history if needed
        val newHistory = if (game.historyIndex < game.history.size - 1) {
            game.history.subList(0, game.historyIndex + 1) + move
        } else {
            game.history + move
        }

        // Check for win
        val isComplete = newBoard.isComplete() && SudokuGenerator.checkSolution(newBoard, game.solution)

        val newGame = game.copy(
            currentBoard = newBoard,
            pencilMarks = newPencilMarks,
            history = newHistory,
            historyIndex = newHistory.size - 1,
            highlightedNumber = value,
            isComplete = isComplete
        )

        gameRepository.saveGame(newGame)
        return Result.success(newGame)
    }
}

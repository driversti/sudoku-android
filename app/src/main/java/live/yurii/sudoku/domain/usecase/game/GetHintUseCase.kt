package live.yurii.sudoku.domain.usecase.game

import live.yurii.sudoku.domain.model.CellPosition
import live.yurii.sudoku.domain.model.Game
import live.yurii.sudoku.domain.model.Move
import live.yurii.sudoku.domain.repository.IGameRepository
import javax.inject.Inject

class GetHintUseCase @Inject constructor(
    private val gameRepository: IGameRepository
) {
    suspend operator fun invoke(game: Game): Result<Game> {
        if (game.isPaused || game.isComplete) {
            return Result.failure(IllegalStateException("Game is paused or complete"))
        }

        // Find first empty non-initial cell
        for (row in 0..8) {
            for (col in 0..8) {
                val index = row * 9 + col
                if (!game.initialCells[index] && game.currentBoard.getCell(CellPosition(row, col)) == null) {
                    val hintValue = game.solution.getCell(CellPosition(row, col))?.value
                        ?: return Result.failure(IllegalStateException("Solution cell is empty"))

                    val move = Move(
                        position = CellPosition(row, col),
                        value = hintValue,
                        previousValue = null,
                        timestamp = System.currentTimeMillis()
                    )

                    val newBoard = game.currentBoard.updateCell(CellPosition(row, col), hintValue)

                    val newGame = game.copy(
                        currentBoard = newBoard,
                        history = game.history + move,
                        historyIndex = game.history.size,
                        selectedCell = CellPosition(row, col),
                        highlightedNumber = hintValue,
                        hintsUsed = game.hintsUsed + 1
                    )

                    gameRepository.saveGame(newGame)
                    return Result.success(newGame)
                }
            }
        }

        return Result.failure(IllegalStateException("No empty cells found"))
    }
}

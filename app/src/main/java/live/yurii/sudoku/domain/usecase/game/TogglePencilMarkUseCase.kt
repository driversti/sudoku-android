package live.yurii.sudoku.domain.usecase.game

import live.yurii.sudoku.domain.model.CellPosition
import live.yurii.sudoku.domain.model.Game
import live.yurii.sudoku.domain.repository.IGameRepository
import javax.inject.Inject

class TogglePencilMarkUseCase @Inject constructor(
    private val gameRepository: IGameRepository
) {
    suspend operator fun invoke(
        game: Game,
        position: CellPosition,
        number: Int
    ): Result<Game> {
        if (game.isPaused || game.isComplete) {
            return Result.failure(IllegalStateException("Game is paused or complete"))
        }

        val index = position.toIndex()
        if (game.initialCells[index]) {
            return Result.failure(IllegalStateException("Cannot modify initial cell"))
        }

        // Don't allow pencil marks if cell has a value
        val currentValue = game.currentBoard.getCell(position)?.value
        if (currentValue != null) {
            return Result.failure(IllegalStateException("Cannot add pencil marks to filled cell"))
        }

        // Toggle the pencil mark
        val currentMarks = game.pencilMarks[index]
        val newMarks = if (number in currentMarks) {
            currentMarks - number
        } else {
            currentMarks + number
        }

        val newPencilMarks = game.pencilMarks.toMutableList().apply {
            this[index] = newMarks
        }

        val newGame = game.copy(
            pencilMarks = newPencilMarks,
            highlightedNumber = number
        )

        gameRepository.saveGame(newGame)
        return Result.success(newGame)
    }
}

package live.yurii.sudoku.domain.usecase.game

import live.yurii.sudoku.domain.model.Game
import live.yurii.sudoku.domain.repository.IGameRepository
import javax.inject.Inject

class UndoUseCase @Inject constructor(
    private val gameRepository: IGameRepository
) {
    suspend operator fun invoke(game: Game): Result<Game> {
        if (!game.canUndo) {
            return Result.failure(IllegalStateException("Nothing to undo"))
        }

        val move = game.history[game.historyIndex]
        val newBoard = game.currentBoard.updateCell(move.position, move.previousValue)

        val newGame = game.copy(
            currentBoard = newBoard,
            historyIndex = game.historyIndex - 1
        )

        gameRepository.saveGame(newGame)
        return Result.success(newGame)
    }
}

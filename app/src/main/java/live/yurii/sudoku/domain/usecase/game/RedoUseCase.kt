package live.yurii.sudoku.domain.usecase.game

import live.yurii.sudoku.domain.model.Game
import live.yurii.sudoku.domain.repository.IGameRepository
import javax.inject.Inject

class RedoUseCase @Inject constructor(
    private val gameRepository: IGameRepository
) {
    suspend operator fun invoke(game: Game): Result<Game> {
        if (!game.canRedo) {
            return Result.failure(IllegalStateException("Nothing to redo"))
        }

        val move = game.history[game.historyIndex + 1]
        val newBoard = game.currentBoard.updateCell(move.position, move.value)

        val newGame = game.copy(
            currentBoard = newBoard,
            historyIndex = game.historyIndex + 1
        )

        gameRepository.saveGame(newGame)
        return Result.success(newGame)
    }
}

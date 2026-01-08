package live.yurii.sudoku.domain.usecase.game

import live.yurii.sudoku.domain.model.Game
import live.yurii.sudoku.domain.repository.IGameRepository
import javax.inject.Inject

class TogglePencilMarkModeUseCase @Inject constructor(
    private val gameRepository: IGameRepository
) {
    suspend operator fun invoke(game: Game): Game {
        val newGame = game.copy(pencilMarkMode = !game.pencilMarkMode)
        gameRepository.saveGame(newGame)
        return newGame
    }
}

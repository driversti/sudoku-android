package live.yurii.sudoku.presentation.screen.game

import live.yurii.sudoku.domain.model.Game

sealed class GameState {
    object Loading : GameState()
    data class Success(val game: Game) : GameState()
    data class Error(val message: String?) : GameState()
}

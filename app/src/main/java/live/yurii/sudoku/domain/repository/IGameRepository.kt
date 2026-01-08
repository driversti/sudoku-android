package live.yurii.sudoku.domain.repository

import live.yurii.sudoku.domain.model.Game
import kotlinx.coroutines.flow.Flow

interface IGameRepository {
    suspend fun saveGame(game: Game)
    suspend fun getCurrentGame(): Game?
    fun getCurrentGameFlow(): Flow<Game?>
    suspend fun clearGame()
    suspend fun deleteGame(gameId: String)
}

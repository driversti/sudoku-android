package live.yurii.sudoku.domain.repository

import live.yurii.sudoku.domain.model.Player
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {
    suspend fun register(username: String, password: String): Result<Player>
    suspend fun login(username: String, password: String): Result<Player>
    suspend fun logout()
    val currentPlayer: Flow<Player?>
    suspend fun getCurrentPlayer(): Player?
}

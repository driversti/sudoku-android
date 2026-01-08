package live.yurii.sudoku.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import live.yurii.sudoku.data.local.preferences.TokenPreferences
import live.yurii.sudoku.data.remote.api.AuthApiService
import live.yurii.sudoku.data.remote.dto.LoginRequest
import live.yurii.sudoku.data.remote.dto.RegisterRequest
import live.yurii.sudoku.domain.model.Player
import live.yurii.sudoku.domain.repository.IAuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenPreferences: TokenPreferences
) : IAuthRepository {

    override suspend fun register(username: String, password: String): Result<Player> {
        return try {
            val response = authApiService.register(RegisterRequest(username, password))
            tokenPreferences.saveToken(response.token)
            tokenPreferences.saveUserInfo(response.player.username, response.player.id)

            Result.success(
                Player(
                    id = response.player.id,
                    username = response.player.username
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(username: String, password: String): Result<Player> {
        return try {
            val response = authApiService.login(LoginRequest(username, password))
            tokenPreferences.saveToken(response.token)
            tokenPreferences.saveUserInfo(response.player.username, response.player.id)

            Result.success(
                Player(
                    id = response.player.id,
                    username = response.player.username
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        tokenPreferences.clear()
    }

    override val currentPlayer: Flow<Player?>
        get() = tokenPreferences.getPlayerId().map { playerId ->
            if (playerId != null) {
                val username = tokenPreferences.getUsername().first()
                Player(id = playerId, username = username ?: "")
            } else {
                null
            }
        }

    override suspend fun getCurrentPlayer(): Player? {
        val playerId = tokenPreferences.getPlayerId().first()
        val username = tokenPreferences.getUsername().first()
        return if (playerId != null && username != null) {
            Player(id = playerId, username = username)
        } else {
            null
        }
    }
}

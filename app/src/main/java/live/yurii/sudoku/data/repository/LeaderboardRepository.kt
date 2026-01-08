package live.yurii.sudoku.data.repository

import live.yurii.sudoku.data.remote.api.LeaderboardApiService
import live.yurii.sudoku.data.remote.dto.SubmitScoreRequest
import live.yurii.sudoku.domain.model.Difficulty
import live.yurii.sudoku.domain.repository.ILeaderboardRepository
import live.yurii.sudoku.domain.repository.LeaderboardEntry
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LeaderboardRepository @Inject constructor(
    private val leaderboardApiService: LeaderboardApiService
) : ILeaderboardRepository {

    override suspend fun getLeaderboard(difficulty: Difficulty, limit: Int): Result<List<LeaderboardEntry>> {
        return try {
            val response = leaderboardApiService.getLeaderboard(difficulty.name.lowercase())
            val entries = response.entries.take(limit).map { dto ->
                LeaderboardEntry(
                    rank = dto.rank,
                    username = dto.username,
                    timeSeconds = (dto.time / 1000).toInt(),
                    moves = 0, // Not provided by API
                    hintsUsed = 0 // Not provided by API
                )
            }
            Result.success(entries)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun submitScore(
        difficulty: Difficulty,
        timeSeconds: Int,
        moves: Int,
        hintsUsed: Int
    ): Result<Unit> {
        return try {
            val request = SubmitScoreRequest(
                difficulty = difficulty.name.lowercase(),
                time = timeSeconds * 1000L, // API expects milliseconds
                hintsUsed = hintsUsed
            )
            leaderboardApiService.submitScore(request)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPlayerBestScores(): Result<Map<Difficulty, LeaderboardEntry?>> {
        // This would require a separate API endpoint
        // For now, return empty result
        return Result.success(emptyMap())
    }
}

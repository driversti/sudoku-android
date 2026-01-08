package live.yurii.sudoku.domain.repository

import live.yurii.sudoku.domain.model.Difficulty

interface ILeaderboardRepository {
    suspend fun getLeaderboard(difficulty: Difficulty, limit: Int): Result<List<LeaderboardEntry>>
    suspend fun submitScore(
        difficulty: Difficulty,
        timeSeconds: Int,
        moves: Int,
        hintsUsed: Int
    ): Result<Unit>

    suspend fun getPlayerBestScores(): Result<Map<Difficulty, LeaderboardEntry?>>
}

data class LeaderboardEntry(
    val rank: Int,
    val username: String,
    val timeSeconds: Int,
    val moves: Int,
    val hintsUsed: Int
)

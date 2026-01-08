package live.yurii.sudoku.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Statistics(
    val difficulty: Difficulty,
    val gamesPlayed: Int,
    val gamesWon: Int,
    val bestTimeMs: Long,
    val currentStreak: Int,
    val bestStreak: Int,
    val totalMoves: Int,
    val totalHintsUsed: Int
) {
    val winRate: Float
        get() = if (gamesPlayed > 0) gamesWon.toFloat() / gamesPlayed else 0f

    fun withGameCompleted(timeMs: Long, moves: Int, hintsUsed: Int): Statistics {
        return copy(
            gamesPlayed = gamesPlayed + 1,
            gamesWon = gamesWon + 1,
            bestTimeMs = minOf(bestTimeMs, timeMs).takeIf { bestTimeMs > 0 } ?: timeMs,
            currentStreak = currentStreak + 1,
            bestStreak = maxOf(bestStreak, currentStreak + 1),
            totalMoves = totalMoves + moves,
            totalHintsUsed = totalHintsUsed + hintsUsed
        )
    }

    fun withGameLost(): Statistics {
        return copy(
            gamesPlayed = gamesPlayed + 1,
            currentStreak = 0
        )
    }

    companion object {
        fun empty(difficulty: Difficulty): Statistics {
            return Statistics(
                difficulty = difficulty,
                gamesPlayed = 0,
                gamesWon = 0,
                bestTimeMs = 0,
                currentStreak = 0,
                bestStreak = 0,
                totalMoves = 0,
                totalHintsUsed = 0
            )
        }
    }
}

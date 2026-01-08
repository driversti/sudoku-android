package live.yurii.sudoku.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import live.yurii.sudoku.data.local.dao.GameDao
import live.yurii.sudoku.data.local.entity.GameEntity
import live.yurii.sudoku.domain.model.Difficulty
import live.yurii.sudoku.domain.model.Statistics
import live.yurii.sudoku.domain.repository.IStatisticsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatisticsRepository @Inject constructor(
    private val gameDao: GameDao
) : IStatisticsRepository {

    override fun getStatistics(difficulty: Difficulty): Flow<Statistics> {
        return gameDao.getCompletedGames()
            .map { games ->
                games
                    .filter { it.difficulty == difficulty.name }
                    .let { filteredGames ->
                        calculateStatistics(difficulty, filteredGames)
                    }
            }
    }

    override fun getAllStatistics(): Flow<Map<Difficulty, Statistics>> {
        return gameDao.getCompletedGames()
            .map { games ->
                Difficulty.entries.associateWith { difficulty ->
                    games
                        .filter { it.difficulty == difficulty.name }
                        .let { filteredGames ->
                            calculateStatistics(difficulty, filteredGames)
                        }
                }
            }
    }

    private fun calculateStatistics(difficulty: Difficulty, games: List<GameEntity>): Statistics {
        if (games.isEmpty()) {
            return Statistics.empty(difficulty)
        }

        val gamesPlayed = games.size
        val gamesWon = games.count { it.isComplete }
        val bestTimeMs = games
            .filter { it.isComplete }
            .minOfOrNull { it.elapsedTime }
            ?: 0L

        // Calculate streaks
        val sortedByTime = games.sortedByDescending { it.lastUpdated }
        var currentStreak = 0
        var bestStreak = 0
        var tempStreak = 0

        // Check current streak (consecutive wins from most recent)
        for (game in sortedByTime) {
            if (game.isComplete) {
                currentStreak++
            } else {
                break
            }
        }

        // Calculate best streak
        for (game in sortedByTime) {
            if (game.isComplete) {
                tempStreak++
                bestStreak = maxOf(bestStreak, tempStreak)
            } else {
                tempStreak = 0
            }
        }

        val totalHintsUsed = games.sumOf { it.hintsUsed }
        // Note: totalMoves would require parsing the history JSON
        // Using game count as placeholder for now
        val totalMoves = games.size * 30  // Estimate: ~30 moves per game

        return Statistics(
            difficulty = difficulty,
            gamesPlayed = gamesPlayed,
            gamesWon = gamesWon,
            bestTimeMs = bestTimeMs,
            currentStreak = currentStreak,
            bestStreak = bestStreak,
            totalMoves = totalMoves,
            totalHintsUsed = totalHintsUsed
        )
    }
}

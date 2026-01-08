package live.yurii.sudoku.domain.repository

import kotlinx.coroutines.flow.Flow

interface IStatisticsRepository {
    fun getStatistics(difficulty: live.yurii.sudoku.domain.model.Difficulty): Flow<live.yurii.sudoku.domain.model.Statistics>
    fun getAllStatistics(): Flow<Map<live.yurii.sudoku.domain.model.Difficulty, live.yurii.sudoku.domain.model.Statistics>>
}

package live.yurii.sudoku.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import live.yurii.sudoku.data.local.entity.StatisticsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StatisticsDao {
    @Query("SELECT * FROM statistics")
    suspend fun getAllStatistics(): List<StatisticsEntity>

    @Query("SELECT * FROM statistics")
    fun getAllStatisticsFlow(): Flow<List<StatisticsEntity>>

    @Query("SELECT * FROM statistics WHERE difficulty = :difficulty")
    suspend fun getStatistics(difficulty: String): StatisticsEntity?

    @Query("SELECT * FROM statistics WHERE difficulty = :difficulty")
    fun getStatisticsFlow(difficulty: String): Flow<StatisticsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateStatistics(stats: StatisticsEntity)

    @Query("DELETE FROM statistics")
    suspend fun clearAllStatistics()
}

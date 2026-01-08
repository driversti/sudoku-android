package live.yurii.sudoku.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import live.yurii.sudoku.data.local.entity.GameEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM games ORDER BY lastUpdated DESC LIMIT 1")
    suspend fun getCurrentGame(): GameEntity?

    @Query("SELECT * FROM games ORDER BY lastUpdated DESC LIMIT 1")
    fun getCurrentGameFlow(): Flow<GameEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGame(game: GameEntity)

    @Query("DELETE FROM games")
    suspend fun clearCurrentGame()

    @Query("DELETE FROM games WHERE id = :gameId")
    suspend fun deleteGame(gameId: String)

    @Query("SELECT * FROM games WHERE isComplete = 1 ORDER BY lastUpdated DESC")
    fun getCompletedGames(): Flow<List<GameEntity>>

    @Query("UPDATE games SET lastUpdated = :timestamp WHERE id = :gameId")
    suspend fun updateLastUpdated(gameId: String, timestamp: Long)
}

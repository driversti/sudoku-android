package live.yurii.sudoku.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import live.yurii.sudoku.data.local.entity.PlayerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM players LIMIT 1")
    suspend fun getCurrentPlayer(): PlayerEntity?

    @Query("SELECT * FROM players LIMIT 1")
    fun getCurrentPlayerFlow(): Flow<PlayerEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePlayer(player: PlayerEntity)

    @Query("UPDATE players SET token = NULL, tokenExpiry = NULL WHERE id = :playerId")
    suspend fun clearToken(playerId: String)

    @Query("DELETE FROM players")
    suspend fun clearPlayer()
}

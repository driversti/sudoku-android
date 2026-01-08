package live.yurii.sudoku.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import live.yurii.sudoku.domain.model.Difficulty

@Entity(tableName = "statistics")
data class StatisticsEntity(
    @PrimaryKey
    val difficulty: String,
    val gamesPlayed: Int,
    val gamesWon: Int,
    val bestTimeMs: Long,
    val currentStreak: Int,
    val bestStreak: Int,
    val totalMoves: Int,
    val totalHintsUsed: Int
)

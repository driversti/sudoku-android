package live.yurii.sudoku.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import live.yurii.sudoku.domain.model.Difficulty

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey
    val id: String,
    val difficulty: String,
    val puzzle: String,        // JSON serialized Board
    val solution: String,      // JSON serialized Board
    val currentBoard: String,  // JSON serialized Board
    val pencilMarks: String,   // JSON serialized List<Set<Int>>
    val initialCells: String,  // JSON serialized List<Boolean>
    val selectedCellRow: Int?,
    val selectedCellCol: Int?,
    val isPaused: Boolean,
    val isComplete: Boolean,
    val hintsUsed: Int,
    val history: String,       // JSON serialized List<Move>
    val historyIndex: Int,
    val startTime: Long,
    val elapsedTime: Long,
    val pencilMarkMode: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
)

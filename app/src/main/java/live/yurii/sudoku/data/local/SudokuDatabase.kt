package live.yurii.sudoku.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import live.yurii.sudoku.data.local.dao.GameDao
import live.yurii.sudoku.data.local.dao.PlayerDao
import live.yurii.sudoku.data.local.dao.StatisticsDao
import live.yurii.sudoku.data.local.entity.GameEntity
import live.yurii.sudoku.data.local.entity.PlayerEntity
import live.yurii.sudoku.data.local.entity.StatisticsEntity

@Database(
    entities = [GameEntity::class, StatisticsEntity::class, PlayerEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SudokuDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
    abstract fun statisticsDao(): StatisticsDao
    abstract fun playerDao(): PlayerDao
}

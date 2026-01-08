package live.yurii.sudoku.di

import android.content.Context
import androidx.room.Room
import live.yurii.sudoku.data.local.SudokuDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideSudokuDatabase(
        @ApplicationContext context: Context
    ): SudokuDatabase {
        return Room.databaseBuilder(
            context,
            SudokuDatabase::class.java,
            "sudoku_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideGameDao(database: SudokuDatabase) = database.gameDao()

    @Provides
    @Singleton
    fun provideStatisticsDao(database: SudokuDatabase) = database.statisticsDao()

    @Provides
    @Singleton
    fun providePlayerDao(database: SudokuDatabase) = database.playerDao()
}

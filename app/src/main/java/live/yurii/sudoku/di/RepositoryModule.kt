package live.yurii.sudoku.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import live.yurii.sudoku.data.repository.AuthRepository
import live.yurii.sudoku.data.repository.GameRepository
import live.yurii.sudoku.data.repository.LeaderboardRepository
import live.yurii.sudoku.data.repository.StatisticsRepository
import live.yurii.sudoku.domain.repository.IAuthRepository
import live.yurii.sudoku.domain.repository.IGameRepository
import live.yurii.sudoku.domain.repository.ILeaderboardRepository
import live.yurii.sudoku.domain.repository.IStatisticsRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGameRepository(repository: GameRepository): IGameRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(repository: AuthRepository): IAuthRepository

    @Binds
    @Singleton
    abstract fun bindLeaderboardRepository(repository: LeaderboardRepository): ILeaderboardRepository

    @Binds
    @Singleton
    abstract fun bindStatisticsRepository(repository: StatisticsRepository): IStatisticsRepository
}

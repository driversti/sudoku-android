package live.yurii.sudoku.di

import live.yurii.sudoku.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApiBaseUrl(): String = BuildConfig.API_BASE_URL
}

package live.yurii.sudoku.presentation.screen.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import live.yurii.sudoku.domain.model.Difficulty
import live.yurii.sudoku.domain.model.Statistics
import live.yurii.sudoku.domain.repository.IStatisticsRepository
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val statisticsRepository: IStatisticsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<StatisticsUiState>(StatisticsUiState.Loading)
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadAllStatistics()
    }

    private fun loadAllStatistics() {
        statisticsRepository.getAllStatistics()
            .onEach { statsMap ->
                _uiState.value = if (statsMap.values.all { it.gamesPlayed == 0 }) {
                    StatisticsUiState.Empty
                } else {
                    StatisticsUiState.Success(statsMap)
                }
            }
            .catch { exception ->
                _uiState.value = StatisticsUiState.Error(
                    exception.message ?: "Failed to load statistics"
                )
            }
            .launchIn(viewModelScope)
    }
}

sealed class StatisticsUiState {
    object Loading : StatisticsUiState()
    object Empty : StatisticsUiState()
    data class Success(
        val statistics: Map<Difficulty, Statistics>
    ) : StatisticsUiState()
    data class Error(val message: String) : StatisticsUiState()
}

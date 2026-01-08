package live.yurii.sudoku.presentation.screen.leaderboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import live.yurii.sudoku.domain.model.Difficulty
import live.yurii.sudoku.domain.repository.ILeaderboardRepository
import live.yurii.sudoku.domain.repository.LeaderboardEntry
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val leaderboardRepository: ILeaderboardRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<LeaderboardUiState>(LeaderboardUiState.Loading)
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    private var currentDifficulty: Difficulty = Difficulty.MEDIUM

    init {
        val difficultyArg = savedStateHandle.get<String>("difficulty")
        currentDifficulty = when (difficultyArg?.uppercase()) {
            "EASY" -> Difficulty.EASY
            "MEDIUM" -> Difficulty.MEDIUM
            "HARD" -> Difficulty.HARD
            "EXPERT" -> Difficulty.EXPERT
            else -> Difficulty.MEDIUM
        }
        loadLeaderboard()
    }

    fun loadLeaderboard() {
        viewModelScope.launch {
            _uiState.value = LeaderboardUiState.Loading

            val result = leaderboardRepository.getLeaderboard(currentDifficulty, 50)

            result.onSuccess { entries ->
                _uiState.value = if (entries.isEmpty()) {
                    LeaderboardUiState.Empty(currentDifficulty)
                } else {
                    LeaderboardUiState.Success(
                        entries = entries,
                        selectedDifficulty = currentDifficulty
                    )
                }
            }.onFailure { exception ->
                _uiState.value = LeaderboardUiState.Error(
                    message = exception.message ?: "Failed to load leaderboard"
                )
            }
        }
    }

    fun onDifficultyChange(difficulty: Difficulty) {
        currentDifficulty = difficulty
        loadLeaderboard()
    }
}

sealed class LeaderboardUiState {
    object Loading : LeaderboardUiState()
    data class Empty(val difficulty: Difficulty) : LeaderboardUiState()
    data class Success(
        val entries: List<LeaderboardEntry>,
        val selectedDifficulty: Difficulty
    ) : LeaderboardUiState()
    data class Error(val message: String) : LeaderboardUiState()
}

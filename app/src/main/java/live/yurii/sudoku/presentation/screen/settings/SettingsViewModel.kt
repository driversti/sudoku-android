package live.yurii.sudoku.presentation.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import live.yurii.sudoku.data.local.preferences.UserSettingsData
import live.yurii.sudoku.data.local.preferences.UserPreferences
import live.yurii.sudoku.domain.model.Theme
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        userPreferences.preferences
            .onEach { preferences ->
                _uiState.value = SettingsUiState.Success(preferences)
            }
            .launchIn(viewModelScope)
    }

    fun setTheme(theme: Theme) {
        viewModelScope.launch {
            userPreferences.setTheme(theme)
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setSoundEnabled(enabled)
        }
    }

    fun setHapticEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setHapticEnabled(enabled)
        }
    }
}

sealed class SettingsUiState {
    object Loading : SettingsUiState()
    data class Success(val preferences: UserSettingsData) : SettingsUiState()
}

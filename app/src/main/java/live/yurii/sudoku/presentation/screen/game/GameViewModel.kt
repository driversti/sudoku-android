package live.yurii.sudoku.presentation.screen.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import live.yurii.sudoku.domain.model.CellPosition
import live.yurii.sudoku.domain.model.Difficulty
import live.yurii.sudoku.domain.repository.IGameRepository
import live.yurii.sudoku.domain.repository.ILeaderboardRepository
import live.yurii.sudoku.domain.usecase.game.GetHintUseCase
import live.yurii.sudoku.domain.usecase.game.NewGameUseCase
import live.yurii.sudoku.domain.usecase.game.RedoUseCase
import live.yurii.sudoku.domain.usecase.game.SelectCellUseCase
import live.yurii.sudoku.domain.usecase.game.SetCellValueUseCase
import live.yurii.sudoku.domain.usecase.game.TogglePencilMarkModeUseCase
import live.yurii.sudoku.domain.usecase.game.TogglePencilMarkUseCase
import live.yurii.sudoku.domain.usecase.game.UndoUseCase
import live.yurii.sudoku.domain.repository.IAuthRepository
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val newGameUseCase: NewGameUseCase,
    private val setCellValueUseCase: SetCellValueUseCase,
    private val undoUseCase: UndoUseCase,
    private val redoUseCase: RedoUseCase,
    private val getHintUseCase: GetHintUseCase,
    private val selectCellUseCase: SelectCellUseCase,
    private val togglePencilMarkModeUseCase: TogglePencilMarkModeUseCase,
    private val togglePencilMarkUseCase: TogglePencilMarkUseCase,
    private val gameRepository: IGameRepository,
    private val authRepository: IAuthRepository,
    private val leaderboardRepository: ILeaderboardRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var timerJob: kotlinx.coroutines.Job? = null

    private val _gameState = MutableStateFlow<GameState>(GameState.Loading)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _isSubmittingScore = MutableStateFlow(false)
    val isSubmittingScore: StateFlow<Boolean> = _isSubmittingScore.asStateFlow()

    // State for showing "continue or restart" dialog
    private val _showNewGameDialog = MutableStateFlow<Difficulty?>(null)
    val showNewGameDialog: StateFlow<Difficulty?> = _showNewGameDialog.asStateFlow()

    // Track if we've already loaded the current game
    private var currentGameJob: kotlinx.coroutines.Job? = null

    init {
        checkLoginStatus()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        // Save current state when leaving to preserve elapsed time
        val currentState = (_gameState.value as? GameState.Success)?.game ?: return
        viewModelScope.launch {
            gameRepository.saveGame(currentState)
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            // Track the initial elapsed time and when we started tracking
            val currentState = (_gameState.value as? GameState.Success)?.game ?: return@launch
            val baseElapsedTime = currentState.elapsedTime
            val timerStartTime = System.currentTimeMillis()

            while (isActive) {
                delay(1000) // Update every second
                val state = (_gameState.value as? GameState.Success)?.game ?: return@launch

                // Only update if game is not paused and not complete
                if (!state.isPaused && !state.isComplete) {
                    // Calculate new elapsed time: base time + time since timer started
                    val newElapsedTime = baseElapsedTime + (System.currentTimeMillis() - timerStartTime)
                    val updatedGame = state.copy(elapsedTime = newElapsedTime)
                    _gameState.value = GameState.Success(updatedGame)

                    // Save every 5 seconds to avoid excessive DB writes
                    if (newElapsedTime / 5000 > state.elapsedTime / 5000) {
                        gameRepository.saveGame(updatedGame)
                    }
                }
            }
        }
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            val player = authRepository.getCurrentPlayer()
            _isLoggedIn.value = player != null
        }
    }

    fun startNewGame(difficulty: Difficulty) {
        viewModelScope.launch {
            val currentGame = gameRepository.getCurrentGame()
            if (currentGame != null && !currentGame.isComplete) {
                // There's an incomplete game, show dialog
                _showNewGameDialog.value = difficulty
            } else {
                // No incomplete game, start new one directly
                forceNewGame(difficulty)
            }
        }
    }

    fun continueCurrentGame() {
        viewModelScope.launch {
            _showNewGameDialog.value = null
            // Load the saved game
            val game = gameRepository.getCurrentGame()
            if (game != null) {
                _gameState.value = GameState.Success(game)
                startTimer()
            }
        }
    }

    fun forceNewGame(difficulty: Difficulty) {
        viewModelScope.launch {
            _showNewGameDialog.value = null
            _gameState.value = GameState.Loading
            val result = newGameUseCase(difficulty)
            result.onSuccess { game ->
                _gameState.value = GameState.Success(game)
                startTimer()
            }.onFailure { exception ->
                _gameState.value = GameState.Error(exception.message)
            }
        }
    }

    fun onCellClick(row: Int, col: Int) {
        viewModelScope.launch {
            val currentState = (_gameState.value as? GameState.Success)?.game ?: return@launch
            val updatedGame = selectCellUseCase(currentState, CellPosition(row, col))
            _gameState.value = GameState.Success(updatedGame)
        }
    }

    fun onNumberInput(number: Int) {
        viewModelScope.launch {
            val currentState = (_gameState.value as? GameState.Success)?.game ?: return@launch
            val selectedCell = currentState.selectedCell ?: return@launch

            val result = if (currentState.pencilMarkMode) {
                togglePencilMarkUseCase(currentState, selectedCell, number)
            } else {
                setCellValueUseCase(currentState, selectedCell, number)
            }

            result.onSuccess { game ->
                _gameState.value = GameState.Success(game)
            }
        }
    }

    fun onClear() {
        viewModelScope.launch {
            val currentState = (_gameState.value as? GameState.Success)?.game ?: return@launch
            val selectedCell = currentState.selectedCell ?: return@launch

            val result = setCellValueUseCase(currentState, selectedCell, null)
            result.onSuccess { game ->
                _gameState.value = GameState.Success(game)
            }
        }
    }

    fun onUndo() {
        viewModelScope.launch {
            val currentState = (_gameState.value as? GameState.Success)?.game ?: return@launch

            val result = undoUseCase(currentState)
            result.onSuccess { game ->
                _gameState.value = GameState.Success(game)
            }
        }
    }

    fun onRedo() {
        viewModelScope.launch {
            val currentState = (_gameState.value as? GameState.Success)?.game ?: return@launch

            val result = redoUseCase(currentState)
            result.onSuccess { game ->
                _gameState.value = GameState.Success(game)
            }
        }
    }

    fun onHint() {
        viewModelScope.launch {
            val currentState = (_gameState.value as? GameState.Success)?.game ?: return@launch

            val result = getHintUseCase(currentState)
            result.onSuccess { game ->
                _gameState.value = GameState.Success(game)
            }
        }
    }

    fun onTogglePencilMarkMode() {
        viewModelScope.launch {
            val currentState = (_gameState.value as? GameState.Success)?.game ?: return@launch
            val updatedGame = togglePencilMarkModeUseCase(currentState)
            _gameState.value = GameState.Success(updatedGame)
        }
    }

    fun onSubmitScore() {
        viewModelScope.launch {
            val currentState = (_gameState.value as? GameState.Success)?.game ?: return@launch

            _isSubmittingScore.value = true
            val result = leaderboardRepository.submitScore(
                difficulty = currentState.difficulty,
                timeSeconds = (currentState.elapsedTime / 1000).toInt(),
                moves = currentState.currentMoves,
                hintsUsed = currentState.hintsUsed
            )

            result.onSuccess {
                _isSubmittingScore.value = false
                // Optionally show success message
            }.onFailure { exception ->
                _isSubmittingScore.value = false
                // Handle error (could show toast or snackbar)
            }
        }
    }
}

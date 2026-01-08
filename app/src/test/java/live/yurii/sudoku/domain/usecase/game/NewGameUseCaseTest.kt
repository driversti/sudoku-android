package live.yurii.sudoku.domain.usecase.game

import kotlinx.coroutines.test.runTest
import live.yurii.sudoku.domain.model.Difficulty
import live.yurii.sudoku.domain.model.Game
import live.yurii.sudoku.domain.repository.IGameRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class NewGameUseCaseTest {

    @Mock
    private lateinit var mockGameRepository: IGameRepository

    private lateinit var newGameUseCase: NewGameUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        newGameUseCase = NewGameUseCase(mockGameRepository)
    }

    @Test
    fun `invoke should create and save game for Easy difficulty`() = runTest {
        // Arrange
        val difficulty = Difficulty.EASY
        doNothing().`when`(mockGameRepository).saveGame(any())

        // Act
        val result = newGameUseCase(difficulty)

        // Assert
        assertTrue(result.isSuccess)
        val game = result.getOrNull()
        assertNotNull(game)
        assertEquals(difficulty, game?.difficulty)
        assertFalse(game?.isComplete ?: true)
        assertEquals(0, game?.hintsUsed)

        // Verify save was called
        verify(mockGameRepository).saveGame(any())
    }

    @Test
    fun `invoke should create and save game for Medium difficulty`() = runTest {
        // Arrange
        val difficulty = Difficulty.MEDIUM
        doNothing().`when`(mockGameRepository).saveGame(any())

        // Act
        val result = newGameUseCase(difficulty)

        // Assert
        assertTrue(result.isSuccess)
        val game = result.getOrNull()
        assertNotNull(game)
        assertEquals(difficulty, game?.difficulty)
    }

    @Test
    fun `invoke should create and save game for Hard difficulty`() = runTest {
        // Arrange
        val difficulty = Difficulty.HARD
        doNothing().`when`(mockGameRepository).saveGame(any())

        // Act
        val result = newGameUseCase(difficulty)

        // Assert
        assertTrue(result.isSuccess)
        val game = result.getOrNull()
        assertNotNull(game)
        assertEquals(difficulty, game?.difficulty)
    }

    @Test
    fun `invoke should create and save game for Expert difficulty`() = runTest {
        // Arrange
        val difficulty = Difficulty.EXPERT
        doNothing().`when`(mockGameRepository).saveGame(any())

        // Act
        val result = newGameUseCase(difficulty)

        // Assert
        assertTrue(result.isSuccess)
        val game = result.getOrNull()
        assertNotNull(game)
        assertEquals(difficulty, game?.difficulty)
    }

    @Test
    fun `invoke should create game with unique puzzle each time`() = runTest {
        // Arrange
        val difficulty = Difficulty.MEDIUM
        doNothing().`when`(mockGameRepository).saveGame(any())

        // Act
        val result1 = newGameUseCase(difficulty)
        val result2 = newGameUseCase(difficulty)

        // Assert
        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)

        val game1 = result1.getOrNull()
        val game2 = result2.getOrNull()

        assertNotNull(game1)
        assertNotNull(game2)

        // Puzzles should be different (no seed)
        assertNotEquals(game1?.puzzle, game2?.puzzle)
    }

    @Test
    fun `invoke should create game with same puzzle when using same seed`() = runTest {
        // Arrange
        val difficulty = Difficulty.MEDIUM
        val seed = "test-seed"
        doNothing().`when`(mockGameRepository).saveGame(any())

        // Act
        val result1 = newGameUseCase(difficulty, seed)
        val result2 = newGameUseCase(difficulty, seed)

        // Assert
        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)

        val game1 = result1.getOrNull()
        val game2 = result2.getOrNull()

        assertNotNull(game1)
        assertNotNull(game2)

        // Puzzles should be the same with same seed
        assertEquals(game1?.puzzle?.cells?.flatten()?.map { it?.value },
                     game2?.puzzle?.cells?.flatten()?.map { it?.value })
    }
}

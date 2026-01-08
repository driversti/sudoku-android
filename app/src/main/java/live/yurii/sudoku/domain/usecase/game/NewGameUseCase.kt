package live.yurii.sudoku.domain.usecase.game

import live.yurii.sudoku.domain.model.Game
import live.yurii.sudoku.domain.repository.IGameRepository
import live.yurii.sudoku.domain.util.SudokuGenerator
import javax.inject.Inject

class NewGameUseCase @Inject constructor(
    private val gameRepository: IGameRepository
) {
    suspend operator fun invoke(
        difficulty: live.yurii.sudoku.domain.model.Difficulty,
        seed: String? = null
    ): Result<Game> {
        return try {
            val puzzle = SudokuGenerator.generate(difficulty, seed)
            val game = Game.create(difficulty, puzzle.initial, puzzle.solution)
            gameRepository.saveGame(game)
            Result.success(game)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

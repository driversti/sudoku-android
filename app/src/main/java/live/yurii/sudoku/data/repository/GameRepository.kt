package live.yurii.sudoku.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import live.yurii.sudoku.data.local.dao.GameDao
import live.yurii.sudoku.data.local.entity.GameEntity
import live.yurii.sudoku.domain.model.Board
import live.yurii.sudoku.domain.model.Cell
import live.yurii.sudoku.domain.model.CellPosition
import live.yurii.sudoku.domain.model.Difficulty
import live.yurii.sudoku.domain.model.Game
import live.yurii.sudoku.domain.model.Move
import live.yurii.sudoku.domain.repository.IGameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(
    private val gameDao: GameDao,
    private val gson: Gson
) : IGameRepository {

    private val moveListType = object : TypeToken<List<Move>>() {}.type
    private val pencilMarksListType = object : TypeToken<List<List<Int>>>() {}.type
    private val boardListType = object : TypeToken<List<List<Int?>>>() {}.type

    override suspend fun saveGame(game: Game) {
        gameDao.saveGame(game.toEntity())
    }

    override suspend fun getCurrentGame(): Game? {
        return gameDao.getCurrentGame()?.toDomain()
    }

    override fun getCurrentGameFlow(): Flow<Game?> {
        return gameDao.getCurrentGameFlow().map { it?.toDomain() }
    }

    override suspend fun clearGame() {
        gameDao.clearCurrentGame()
    }

    override suspend fun deleteGame(gameId: String) {
        gameDao.deleteGame(gameId)
    }

    // Extension functions for conversion

    private fun Game.toEntity(): GameEntity {
        return GameEntity(
            id = id,
            difficulty = difficulty.name,
            puzzle = gson.toJson(boardToList(puzzle)),
            solution = gson.toJson(boardToList(solution)),
            currentBoard = gson.toJson(boardToList(currentBoard)),
            pencilMarks = gson.toJson(pencilMarks.map { row -> row.toList() }),
            initialCells = gson.toJson(initialCells),
            selectedCellRow = selectedCell?.row,
            selectedCellCol = selectedCell?.col,
            isPaused = isPaused,
            isComplete = isComplete,
            hintsUsed = hintsUsed,
            history = gson.toJson(history),
            historyIndex = historyIndex,
            startTime = startTime,
            elapsedTime = elapsedTime,
            pencilMarkMode = pencilMarkMode
        )
    }

    private fun GameEntity.toDomain(): Game {
        val puzzleList: List<List<Int?>> = gson.fromJson(puzzle, boardListType)
        val solutionList: List<List<Int?>> = gson.fromJson(solution, boardListType)
        val currentBoardList: List<List<Int?>> = gson.fromJson(currentBoard, boardListType)
        val pencilMarksList: List<List<Int>> = gson.fromJson(pencilMarks, pencilMarksListType)
        val historyList: List<Move> = gson.fromJson(history, moveListType)

        return Game(
            id = id,
            difficulty = Difficulty.valueOf(difficulty),
            puzzle = listToBoard(puzzleList),
            solution = listToBoard(solutionList),
            currentBoard = listToBoard(currentBoardList),
            pencilMarks = pencilMarksList.map { row -> row.toSet() },
            initialCells = gson.fromJson(initialCells, Array<Boolean>::class.java).toList(),
            selectedCell = if (selectedCellRow != null && selectedCellCol != null) {
                CellPosition(selectedCellRow, selectedCellCol)
            } else null,
            highlightedNumber = null, // Not persisted
            isPaused = isPaused,
            isComplete = isComplete,
            hintsUsed = hintsUsed,
            history = historyList,
            historyIndex = historyIndex,
            startTime = startTime,
            elapsedTime = elapsedTime,
            pencilMarkMode = pencilMarkMode
        )
    }

    private fun boardToList(board: Board): List<List<Int?>> {
        return board.cells.map { row ->
            row.map { cell -> cell?.value }
        }
    }

    private fun listToBoard(list: List<List<Int?>>): Board {
        return Board(list.map { row ->
            row.map { value ->
                if (value != null) Cell(value) else null
            }
        })
    }
}

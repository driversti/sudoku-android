package live.yurii.sudoku.domain.util

import live.yurii.sudoku.domain.model.Board
import live.yurii.sudoku.domain.model.Cell
import live.yurii.sudoku.domain.model.CellPosition
import live.yurii.sudoku.domain.model.Difficulty
import kotlin.random.Random

/**
 * Seeded random number generator for consistent daily challenges
 */
private class SeededRandom(private val seed: String) : Random() {
    private var state: Int = seed.hashCode().takeIf { it != 0 } ?: 1

    override fun nextBits(bitCount: Int): Int {
        state = (state * 1103515245 + 12345) and 0x7fffffff
        return state.ushr(31 - bitCount)
    }
}

/**
 * Result of puzzle generation
 */
data class Puzzle(
    val initial: Board,
    val solution: Board,
    val difficulty: Difficulty,
    val seed: String? = null
)

/**
 * Sudoku puzzle generator and solver
 */
object SudokuGenerator {

    /**
     * Generate a new Sudoku puzzle with the given difficulty
     * @param difficulty The difficulty level
     * @param seed Optional seed for reproducible puzzles (e.g., daily challenges)
     */
    fun generate(difficulty: Difficulty, seed: String? = null): Puzzle {
        val random = if (seed != null) SeededRandom(seed) else Random

        val solution = generateCompleteSolution(random)
        val cellsToReveal = difficulty.cellsToReveal.random(random)
        val initial = createPuzzleFromSolution(solution, cellsToReveal, random)

        return Puzzle(initial, solution, difficulty, seed)
    }

    /**
     * Generate a complete valid Sudoku solution
     */
    private fun generateCompleteSolution(random: Random): Board {
        val board = Board.empty()

        // Fill diagonal 3x3 boxes first (independent of each other)
        for (box in 0..8 step 3) {
            fillBox(board, box, box, random)
        }

        // Solve the rest using backtracking
        val mutableBoard = toMutableBoard(board)
        solveSudoku(mutableBoard)

        return fromMutableBoard(mutableBoard)
    }

    /**
     * Fill a 3x3 box with random numbers 1-9
     */
    private fun fillBox(board: Board, startRow: Int, startCol: Int, random: Random) {
        val numbers = (1..9).shuffled(random)
        var index = 0

        val mutableBoard = toMutableBoard(board)

        for (r in 0..2) {
            for (c in 0..2) {
                mutableBoard[startRow + r][startCol + c] = Cell(numbers[index])
                index++
            }
        }
    }

    /**
     * Create a puzzle by removing cells from a complete solution
     * Ensures the puzzle has a unique solution
     */
    private fun createPuzzleFromSolution(
        solution: Board,
        cellsToKeep: Int,
        random: Random
    ): Board {
        val puzzle = toMutableBoard(solution)
        val totalCells = 81
        val cellsToRemove = totalCells - cellsToKeep

        // Get all cell positions and shuffle them
        val positions = (0..8).flatMap { row -> (0..8).map { col -> row to col } }
            .shuffled(random)

        var removed = 0
        for ((row, col) in positions) {
            if (removed >= cellsToRemove) break

            val backup = puzzle[row][col]
            puzzle[row][col] = null

            // Check if puzzle still has unique solution
            val testBoard = deepCopyMutable(puzzle)
            if (countSolutions(testBoard, 2) == 1) {
                removed++
            } else {
                // Revert the removal
                puzzle[row][col] = backup
            }
        }

        return fromMutableBoard(puzzle)
    }

    /**
     * Check if placing a number at a position is valid
     */
    fun isValidPlacement(board: List<List<Cell?>>, row: Int, col: Int, num: Int): Boolean {
        // Check row
        for (c in 0..8) {
            if (board[row][c]?.value == num) return false
        }

        // Check column
        for (r in 0..8) {
            if (board[r][col]?.value == num) return false
        }

        // Check 3x3 box
        val boxRow = (row / 3) * 3
        val boxCol = (col / 3) * 3

        for (r in boxRow until boxRow + 3) {
            for (c in boxCol until boxCol + 3) {
                if (board[r][c]?.value == num) return false
            }
        }

        return true
    }

    /**
     * Find an empty cell (returns null if board is full)
     */
    private fun findEmptyCell(board: List<List<Cell?>>): Pair<Int, Int>? {
        for (r in 0..8) {
            for (c in 0..8) {
                if (board[r][c] == null) return r to c
            }
        }
        return null
    }

    /**
     * Solve Sudoku using backtracking algorithm
     * Returns true if solvable, false otherwise
     * Modifies the board in place with the solution
     */
    private fun solveSudoku(board: MutableList<MutableList<Cell?>>): Boolean {
        val emptyCell = findEmptyCell(board) ?: return true

        val (row, col) = emptyCell

        // Try numbers 1-9
        for (num in 1..9) {
            if (isValidPlacement(board, row, col, num)) {
                board[row][col] = Cell(num)

                if (solveSudoku(board)) {
                    return true
                }

                // Backtrack
                board[row][col] = null
            }
        }

        return false
    }

    /**
     * Count the number of solutions for a given board
     * Used to verify unique solution
     */
    private fun countSolutions(board: MutableList<MutableList<Cell?>>, limit: Int): Int {
        val emptyCell = findEmptyCell(board) ?: return 1

        val (row, col) = emptyCell
        var count = 0

        for (num in 1..9) {
            if (isValidPlacement(board, row, col, num)) {
                board[row][col] = Cell(num)
                count += countSolutions(board, limit - count)
                board[row][col] = null

                if (count >= limit) {
                    return count
                }
            }
        }

        return count
    }

    /**
     * Get all pencil mark candidates for a cell
     */
    fun getCandidates(board: Board, row: Int, col: Int): List<Int> {
        if (board.getCell(CellPosition(row, col)) != null) return emptyList()

        val candidates = mutableListOf<Int>()
        for (num in 1..9) {
            if (isValidPlacement(board.cells, row, col, num)) {
                candidates.add(num)
            }
        }
        return candidates
    }

    /**
     * Check if a board is completely filled
     */
    fun isBoardComplete(board: Board): Boolean {
        return board.cells.all { row -> row.all { it != null } }
    }

    /**
     * Check if the current board matches the solution
     */
    fun checkSolution(board: Board, solution: Board): Boolean {
        return board.cells.zip(solution.cells).all { (r1, r2) ->
            r1.zip(r2).all { (c1, c2) -> c1?.value == c2?.value }
        }
    }

    // Helper functions for mutable board conversion

    private fun toMutableBoard(board: Board): MutableList<MutableList<Cell?>> {
        return board.cells.map { row ->
            row.mapTo(mutableListOf()) { it?.let { Cell(it.value, it.isInitial, it.pencilMarks) } }
        }.toMutableList()
    }

    private fun fromMutableBoard(mutableBoard: MutableList<MutableList<Cell?>>): Board {
        return Board(
            mutableBoard.map { row ->
                row.map { cell ->
                    cell?.let { Cell(it.value, false) }
                }
            }
        )
    }

    private fun deepCopyMutable(board: MutableList<MutableList<Cell?>>): MutableList<MutableList<Cell?>> {
        return board.map { row ->
            row.mapTo(mutableListOf()) { it?.copy() }
        }.toMutableList()
    }
}

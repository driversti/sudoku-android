package live.yurii.sudoku.domain.util

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SudokuGeneratorTest {

    @Test
    fun `generate should create a valid puzzle`() {
        val puzzle = SudokuGenerator.generate(
            live.yurii.sudoku.domain.model.Difficulty.MEDIUM
        )

        assertNotNull(puzzle.initial)
        assertNotNull(puzzle.solution)
        assertEquals(live.yurii.sudoku.domain.model.Difficulty.MEDIUM, puzzle.difficulty)
    }

    @Test
    fun `solution should be a complete valid board`() {
        val puzzle = SudokuGenerator.generate(
            live.yurii.sudoku.domain.model.Difficulty.MEDIUM
        )

        // Check that solution is complete
        assertTrue(SudokuGenerator.isBoardComplete(puzzle.solution))

        // Check that solution is valid (no duplicates in rows, columns, boxes)
        for (row in 0..8) {
            for (col in 0..8) {
                val cell = puzzle.solution.cells[row][col]
                assertNotNull(cell)

                // Check this cell doesn't conflict with others
                val testBoard = puzzle.solution.cells.map { it.map { c -> c?.value } }
                assertTrue(
                    SudokuGenerator.isValidPlacement(testBoard, row, col, cell!!.value)
                )
            }
        }
    }

    @Test
    fun `puzzle should have fewer cells than solution`() {
        val puzzle = SudokuGenerator.generate(
            live.yurii.sudoku.domain.model.Difficulty.MEDIUM
        )

        var puzzleCellCount = 0
        var solutionCellCount = 0

        for (row in 0..8) {
            for (col in 0..8) {
                if (puzzle.initial.cells[row][col] != null) puzzleCellCount++
                if (puzzle.solution.cells[row][col] != null) solutionCellCount++
            }
        }

        assertTrue(solutionCellCount > puzzleCellCount)
        assertEquals(81, solutionCellCount) // Solution should be complete
    }

    @Test
    fun `seeded generation should produce same puzzle`() {
        val seed = "test-seed-123"
        val difficulty = live.yurii.sudoku.domain.model.Difficulty.EASY

        val puzzle1 = SudokuGenerator.generate(difficulty, seed)
        val puzzle2 = SudokuGenerator.generate(difficulty, seed)

        // Compare board cells
        for (row in 0..8) {
            for (col in 0..8) {
                val cell1 = puzzle1.initial.cells[row][col]
                val cell2 = puzzle2.initial.cells[row][col]

                if (cell1 == null) {
                    assertNull(cell2)
                } else {
                    assertNotNull(cell2)
                    assertEquals(cell1.value, cell2!!.value)
                }
            }
        }
    }

    @Test
    fun `getCandidates should return valid numbers only`() {
        val puzzle = SudokuGenerator.generate(
            live.yurii.sudoku.domain.model.Difficulty.EASY
        )

        // Pick a random empty cell
        val emptyCell = findEmptyCell(puzzle.initial)
        if (emptyCell != null) {
            val (row, col) = emptyCell
            val candidates = SudokuGenerator.getCandidates(puzzle.initial, row, col)

            // Each candidate should be a valid placement
            for (candidate in candidates) {
                val testBoard = puzzle.initial.cells.map { it.map { c -> c?.value } }
                assertTrue(
                    SudokuGenerator.isValidPlacement(testBoard, row, col, candidate),
                    "Candidate $candidate at ($row, $col) should be valid"
                )
            }
        }
    }

    @Test
    fun `isValidPlacement should detect invalid row placement`() {
        val puzzle = SudokuGenerator.generate(
            live.yurii.sudoku.domain.model.Difficulty.EASY
        )

        // Find a cell with a value
        val (testRow, testCol) = findFilledCell(puzzle.solution) ?: return

        // Try to place same number in same row (different column)
        val value = puzzle.solution.cells[testRow][testCol]?.value ?: return
        val otherCol = (testCol + 1) % 9
        val otherCell = puzzle.solution.cells[testRow][otherCol]

        // Clear that cell first
        val testBoard = puzzle.solution.cells.map {
            it.map { cell ->
                if (cell?.value == value && it.indexOf(cell) == otherCol) null
                else cell?.value
            }
        }

        // Should not be valid if row already has this number
        val hasNumberInRow = testBoard[testRow].any { it == value }
        if (hasNumberInRow) {
            assertFalse(
                SudokuGenerator.isValidPlacement(testBoard, testRow, otherCol, value),
                "Should not allow duplicate in row"
            )
        }
    }

    @Test
    fun `checkSolution should verify correct solution`() {
        val puzzle = SudokuGenerator.generate(
            live.yurii.sudoku.domain.model.Difficulty.MEDIUM
        )

        // Solution should match itself
        assertTrue(
            SudokuGenerator.checkSolution(puzzle.solution, puzzle.solution)
        )
    }

    private fun findEmptyCell(board: live.yurii.sudoku.domain.model.Board): Pair<Int, Int>? {
        for (row in 0..8) {
            for (col in 0..8) {
                if (board.cells[row][col] == null) {
                    return row to col
                }
            }
        }
        return null
    }

    private fun findFilledCell(board: live.yurii.sudoku.domain.model.Board): Pair<Int, Int>? {
        for (row in 0..8) {
            for (col in 0..8) {
                if (board.cells[row][col] != null) {
                    return row to col
                }
            }
        }
        return null
    }
}

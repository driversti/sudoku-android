package live.yurii.sudoku.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Cell(
    val value: Int,
    val isInitial: Boolean = false,
    val pencilMarks: Set<Int> = emptySet()
) {
    init {
        require(value in 0..9) { "Cell value must be between 0 and 9" }
    }

    companion object {
        val Empty = Cell(0)
    }
}

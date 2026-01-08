package live.yurii.sudoku.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CellPosition(
    val row: Int,
    val col: Int
) {
    init {
        require(row in 0..8) { "Row must be between 0 and 8" }
        require(col in 0..8) { "Column must be between 0 and 8" }
    }

    fun toIndex(): Int = row * 9 + col

    companion object {
        fun fromIndex(index: Int): CellPosition {
            require(index in 0..80) { "Index must be between 0 and 80" }
            return CellPosition(index / 9, index % 9)
        }
    }
}

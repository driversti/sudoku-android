package live.yurii.sudoku.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Move(
    val position: CellPosition,
    val value: Int?,
    val previousValue: Int?,
    val timestamp: Long
)

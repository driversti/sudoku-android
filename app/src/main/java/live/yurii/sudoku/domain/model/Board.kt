package live.yurii.sudoku.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Board(
    val cells: List<List<Cell?>>
) {
    companion object {
        fun empty(): Board = Board(List(9) { List(9) { null } })

        fun fromFlatList(list: List<Int?>): Board {
            require(list.size == 81) { "Board must have exactly 81 cells" }
            return Board(
                List(9) { row ->
                    List(9) { col ->
                        val value = list[row * 9 + col]
                        if (value != null && value > 0) Cell(value) else null
                    }
                }
            )
        }
    }

    fun getCell(position: CellPosition): Cell? = cells[position.row][position.col]

    fun updateCell(position: CellPosition, value: Int?): Board {
        val newCells = cells.mapIndexed { row, r ->
            r.mapIndexed { col, cell ->
                if (row == position.row && col == position.col) {
                    if (value != null && value > 0) Cell(value) else null
                } else {
                    cell
                }
            }
        }
        return copy(cells = newCells)
    }

    fun isComplete(): Boolean {
        return cells.all { row -> row.all { it != null } }
    }

    fun matches(other: Board): Boolean {
        return cells.zip(other.cells).all { (r1, r2) ->
            r1.zip(r2).all { (c1, c2) -> c1?.value == c2?.value }
        }
    }

    fun toFlatList(): List<Int?> {
        return cells.flatten().map { it?.value }
    }
}

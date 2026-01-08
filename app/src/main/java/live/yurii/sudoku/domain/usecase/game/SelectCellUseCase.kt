package live.yurii.sudoku.domain.usecase.game

import live.yurii.sudoku.domain.model.CellPosition
import live.yurii.sudoku.domain.model.Game
import javax.inject.Inject

class SelectCellUseCase @Inject constructor() {
    operator fun invoke(game: Game, position: CellPosition): Game {
        // Update highlighted number if cell has a value
        val cellValue = game.currentBoard.getCell(position)?.value
        val newHighlightedNumber = cellValue ?: game.highlightedNumber

        return game.copy(
            selectedCell = position,
            highlightedNumber = newHighlightedNumber
        )
    }
}

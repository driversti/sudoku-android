package live.yurii.sudoku.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Player(
    val id: String,
    val username: String
)

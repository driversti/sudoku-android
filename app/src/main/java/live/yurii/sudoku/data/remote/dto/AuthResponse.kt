package live.yurii.sudoku.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("token")
    val token: String,
    @SerializedName("player")
    val player: PlayerDto
)

data class PlayerDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("username")
    val username: String
)

data class LoginRequest(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String
)

data class RegisterRequest(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String
)

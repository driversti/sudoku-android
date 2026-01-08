package live.yurii.sudoku.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LeaderboardResponse(
    @SerializedName("entries")
    val entries: List<LeaderboardEntryDto>
)

data class LeaderboardEntryDto(
    @SerializedName("rank")
    val rank: Int,
    @SerializedName("username")
    val username: String,
    @SerializedName("time")
    val time: Long,
    @SerializedName("isCurrentUser")
    val isCurrentUser: Boolean = false
)

data class SubmitScoreRequest(
    @SerializedName("difficulty")
    val difficulty: String,
    @SerializedName("time")
    val time: Long,
    @SerializedName("hintsUsed")
    val hintsUsed: Int
)

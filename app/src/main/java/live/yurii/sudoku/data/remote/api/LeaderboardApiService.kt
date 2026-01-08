package live.yurii.sudoku.data.remote.api

import live.yurii.sudoku.data.remote.dto.LeaderboardResponse
import live.yurii.sudoku.data.remote.dto.SubmitScoreRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface LeaderboardApiService {

    @GET("leaderboard/{difficulty}")
    suspend fun getLeaderboard(@Path("difficulty") difficulty: String): LeaderboardResponse

    @POST("leaderboard")
    suspend fun submitScore(@Body request: SubmitScoreRequest)
}

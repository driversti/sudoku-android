package live.yurii.sudoku.presentation.navigation

sealed class NavRoute(val route: String) {
    object Welcome : NavRoute("welcome")
    object Game : NavRoute("game/{difficulty}") {
        fun createRoute(difficulty: String) = "game/$difficulty"
    }
    object Leaderboard : NavRoute("leaderboard/{difficulty}") {
        fun createRoute(difficulty: String) = "leaderboard/$difficulty"
    }
    object Statistics : NavRoute("statistics")
    object Settings : NavRoute("settings")
    object Login : NavRoute("login")
    object Register : NavRoute("register")
    object DailyChallenge : NavRoute("daily/{date}/{difficulty}") {
        fun createRoute(date: String, difficulty: String) = "daily/$date/$difficulty"
    }
}

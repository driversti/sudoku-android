package live.yurii.sudoku.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import live.yurii.sudoku.domain.model.Difficulty
import live.yurii.sudoku.presentation.screen.auth.LoginScreen
import live.yurii.sudoku.presentation.screen.auth.RegisterScreen
import live.yurii.sudoku.presentation.screen.daily.DailyChallengeScreen
import live.yurii.sudoku.presentation.screen.game.GameScreen
import live.yurii.sudoku.presentation.screen.leaderboard.LeaderboardScreen
import live.yurii.sudoku.presentation.screen.settings.SettingsScreen
import live.yurii.sudoku.presentation.screen.statistics.StatisticsScreen
import live.yurii.sudoku.presentation.screen.welcome.WelcomeScreen

@Composable
fun SudokuNavigation(
    navController: NavHostController = androidx.navigation.compose.rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = NavRoute.Welcome.route
    ) {
        composable(NavRoute.Welcome.route) {
            WelcomeScreen(
                onStartGame = { difficulty ->
                    navController.navigate(NavRoute.Game.createRoute(difficulty.name))
                },
                onLeaderboardClick = {
                    navController.navigate(NavRoute.Leaderboard.createRoute(Difficulty.MEDIUM.name))
                },
                onStatsClick = {
                    navController.navigate(NavRoute.Statistics.route)
                },
                onSettingsClick = {
                    navController.navigate(NavRoute.Settings.route)
                },
                onProfileClick = {
                    navController.navigate(NavRoute.Login.route)
                }
            )
        }

        composable(
            route = NavRoute.Game.route,
            arguments = listOf(
                navArgument("difficulty") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val difficultyStr = backStackEntry.arguments?.getString("difficulty")
            val difficulty = when (difficultyStr?.uppercase()) {
                "EASY" -> Difficulty.EASY
                "HARD" -> Difficulty.HARD
                "EXPERT" -> Difficulty.EXPERT
                else -> Difficulty.MEDIUM
            }
            GameScreen(
                onBack = { navController.popBackStack() },
                difficulty = difficulty
            )
        }

        composable(
            route = NavRoute.Leaderboard.route,
            arguments = listOf(
                navArgument("difficulty") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val difficulty = backStackEntry.arguments?.getString("difficulty") ?: "MEDIUM"
            LeaderboardScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoute.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.popBackStack()
                },
                onNavigateToRegister = {
                    navController.navigate(NavRoute.Register.route)
                }
            )
        }

        composable(NavRoute.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.popBackStack()
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(NavRoute.Statistics.route) {
            StatisticsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(NavRoute.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = NavRoute.DailyChallenge.route,
            arguments = listOf(
                navArgument("date") { type = NavType.StringType },
                navArgument("difficulty") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date")
            val difficultyStr = backStackEntry.arguments?.getString("difficulty")
            val difficulty = when (difficultyStr?.uppercase()) {
                "EASY" -> Difficulty.EASY
                "HARD" -> Difficulty.HARD
                "EXPERT" -> Difficulty.EXPERT
                else -> Difficulty.MEDIUM
            }
            DailyChallengeScreen(
                onBack = { navController.popBackStack() },
                date = date,
                initialDifficulty = difficulty,
                navController = navController
            )
        }
    }
}

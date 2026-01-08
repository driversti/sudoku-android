package live.yurii.sudoku.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.runtime.SideEffect
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import live.yurii.sudoku.presentation.navigation.SudokuNavigation
import live.yurii.sudoku.presentation.common.theme.SudokuTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SudokuTheme {
                val systemUiController = rememberSystemUiController()
                val colors = MaterialTheme.colorScheme

                // Set system bar colors
                SideEffect {
                    systemUiController.setSystemBarsColor(
                        color = colors.background
                    )
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colors.background
                ) {
                    SudokuNavigation()
                }
            }
        }
    }
}

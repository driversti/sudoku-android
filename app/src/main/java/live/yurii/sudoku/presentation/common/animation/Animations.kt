package live.yurii.sudoku.presentation.common.animation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

/**
 * Standard animation durations in milliseconds
 */
object AnimationDuration {
    const val FAST = 150
    const val MEDIUM = 300
    const val SLOW = 500
}

/**
 * Standard enter transitions for screen navigation
 */
fun slideInHorizontally(
    initialOffsetX: (Int) -> Int = { it },
    animationDurationMillis: Int = AnimationDuration.MEDIUM
): EnterTransition {
    return slideInHorizontally(
        initialOffsetX = initialOffsetX,
        animationSpec = tween(durationMillis = animationDurationMillis)
    )
}

/**
 * Standard exit transitions for screen navigation
 */
fun slideOutHorizontally(
    targetOffsetX: (Int) -> Int = { -it },
    animationDurationMillis: Int = AnimationDuration.MEDIUM
): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = targetOffsetX,
        animationSpec = tween(durationMillis = animationDurationMillis)
    )
}

/**
 * Fade in transition
 */
fun fadeInTransition(animationDurationMillis: Int = AnimationDuration.FAST): EnterTransition {
    return fadeIn(
        animationSpec = tween(durationMillis = animationDurationMillis)
    )
}

/**
 * Fade out transition
 */
fun fadeOutTransition(animationDurationMillis: Int = AnimationDuration.FAST): ExitTransition {
    return fadeOut(
        animationSpec = tween(durationMillis = animationDurationMillis)
    )
}

/**
 * Combined fade transition for enter
 */
fun fadeTransition(): AnimatedContentTransitionScope<*>.() -> EnterTransition? = {
    fadeInTransition(animationDurationMillis = AnimationDuration.MEDIUM) +
    slideInHorizontally(initialOffsetX = { it / 2 }, animationDurationMillis = AnimationDuration.MEDIUM)
}

/**
 * Combined fade transition for exit
 */
fun fadeTransitionExit(): AnimatedContentTransitionScope<*>.() -> ExitTransition? = {
    fadeOutTransition(animationDurationMillis = AnimationDuration.MEDIUM) +
    slideOutHorizontally(targetOffsetX = { -it / 2 }, animationDurationMillis = AnimationDuration.MEDIUM)
}

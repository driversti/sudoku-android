package live.yurii.sudoku.domain.model

enum class Difficulty(val cellsToReveal: IntRange, val displayName: String) {
    EASY(36..40, "Easy"),
    MEDIUM(30..34, "Medium"),
    HARD(24..29, "Hard"),
    EXPERT(17..23, "Expert");

    companion object {
        fun fromString(value: String): Difficulty? {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
        }
    }
}

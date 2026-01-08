package live.yurii.sudoku.domain.model

enum class Theme {
    LIGHT, DARK, SEPIA, BLUE;

    val displayName: String
        get() = name.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

    companion object {
        fun fromString(value: String): Theme {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: LIGHT
        }
    }
}

package by.overpas.detekt.intellij.jvm.logging

internal enum class LimitLevelType {
    ALL,
    WARN_AND_LOWER,
    INFO_AND_LOWER,
    DEBUG_AND_LOWER,
    TRACE,
    ;

    fun skips(level: String?): Boolean {
        if (this == ALL) return false
        val isReported = when (level) {
            null, LevelType.FATAL.name, LevelType.ERROR.name -> false
            LevelType.WARN.name -> this == WARN_AND_LOWER
            LevelType.INFO.name -> ordinal <= INFO_AND_LOWER.ordinal
            LevelType.DEBUG.name -> ordinal <= DEBUG_AND_LOWER.ordinal
            else -> true
        }
        return !isReported
    }
}

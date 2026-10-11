package by.overpas.detekt.intellij.jvm.general.mustalreadyberemovedapi

internal enum class MustAlreadyBeRemovedApiToken(private val priority: Int) {
    SNAP(10),
    SNAPSHOT(10),
    M(20),
    EAP(25),
    PRE(25),
    PREVIEW(25),
    ALPHA(30),
    A(30),
    BETA(40),
    BETTA(40),
    B(40),
    RC(50),
    WHITESPACE(60),
    SP(70),
    REL(80),
    RELEASE(80),
    R(80),
    FINAL(80),
    WORD(90),
    DIGITS(100),
    BUNDLED(666),
    ;

    companion object {
        private val generic = setOf(WHITESPACE, WORD, DIGITS)

        fun compare(
            first: String?,
            second: String?,
        ): Int {
            val firstType = lookup(first)
            val byPriority = firstType.priority.compareTo(lookup(second).priority)
            return when {
                byPriority != 0 || first == null || second == null -> byPriority
                firstType == WORD -> first.compareTo(second)
                firstType == DIGITS -> compareRemovalVersionNumbers(first, second)
                else -> 0
            }
        }

        private fun lookup(token: String?): MustAlreadyBeRemovedApiToken {
            val trimmed = token?.trim().orEmpty()
            return entries.firstOrNull { it !in generic && it.name.equals(trimmed, ignoreCase = true) }
                ?: when {
                    trimmed.isEmpty() || trimmed.all { it == '0' } -> WHITESPACE
                    trimmed.all { it.isDigit() } -> DIGITS
                    else -> WORD
                }
        }
    }
}

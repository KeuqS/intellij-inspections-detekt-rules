package by.overpas.detekt.intellij.jvm.testframeworks.testmethodwithoutassertion

internal data class TestMethodWithoutAssertionMethod(
    val owner: String,
    val name: Regex,
) {

    companion object {
        fun parse(entry: String): TestMethodWithoutAssertionMethod? {
            val owner = entry.substringBefore('#', missingDelimiterValue = "")
            val name = entry.substringAfter('#', missingDelimiterValue = "")
            return if (owner.isBlank() || name.isBlank()) null else TestMethodWithoutAssertionMethod(owner, Regex(name))
        }
    }
}

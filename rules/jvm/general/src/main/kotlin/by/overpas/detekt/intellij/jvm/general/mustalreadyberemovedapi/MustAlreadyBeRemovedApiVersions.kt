package by.overpas.detekt.intellij.jvm.general.mustalreadyberemovedapi

internal object MustAlreadyBeRemovedApiVersions : Comparator<String> {

    private val separators = Regex("[()._\\-;:/, +~]+")
    private val words = Regex("\\d+|\\D+")

    override fun compare(
        first: String,
        second: String,
    ): Int {
        val firstTokens = tokens(first)
        val secondTokens = tokens(second)
        return (0..<maxOf(firstTokens.size, secondTokens.size))
            .asSequence()
            .map { MustAlreadyBeRemovedApiToken.compare(firstTokens.getOrNull(it), secondTokens.getOrNull(it)) }
            .firstOrNull { it != 0 }
            ?: 0
    }

    private fun tokens(version: String): List<String> =
        version.lowercase().trim().split(separators)
            .filter { it.isNotEmpty() }
            .flatMap { part -> words.findAll(part).map { it.value }.toList() }
}

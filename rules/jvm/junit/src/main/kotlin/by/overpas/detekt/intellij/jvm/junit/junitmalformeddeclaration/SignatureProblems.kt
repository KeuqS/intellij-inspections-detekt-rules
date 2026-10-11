package by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration

internal class SignatureProblems(
    val modifiers: List<String>,
    val type: String?,
    val parameters: List<String>,
) {

    fun isEmpty(): Boolean =
        modifiers.isEmpty() && type == null && parameters.isEmpty()

    fun clauses(): List<String> {
        val beParts = modifiers + listOfNotNull(type?.let { "of type '$it'" })
        return beParts.mapIndexed { index, part -> if (index == 0) "be $part" else part } +
            listOfNotNull(parameterClause())
    }

    private fun parameterClause(): String? =
        when (parameters.size) {
            0 -> null

            1 -> "not declare parameter '${parameters.single()}'"

            else -> "not declare parameters ${parameters.dropLast(
                1,
            ).joinToString { "'$it'" }} and '${parameters.last()}'"
        }
}

package by.overpas.detekt.intellij.jvm.logging.loggingplaceholdercountmatchesargumentcount

private const val CONVERSIONS = "bBhHsScCdoxXeEfgGaA%n"
private const val DATE_CONVERSIONS = "HIklMSLNpzZsQBbhAaCYyjmdeRTrDFc"
private val SPECIFIER = Regex("""%(\d+\$)?([-#+ 0,(<]*)?(\d+)?(\.\d+)?([tT])?([a-zA-Z%])""")
private val INCOMPLETE_SPECIFIER = Regex("""%(\d+\$?)?[-#+ 0,(<]*\d*(\.\d*)?[tT]?$""")

internal class FormatSpecifiers(
    private val format: String,
    private val isFull: Boolean,
) {

    fun count(): Int? {
        val positions = generateSequence(format.indexOf('%').takeIf { it != -1 }) { position ->
            SPECIFIER.matchAt(format, position)?.run { format.indexOf('%', range.last + 1).takeIf { it != -1 } }
        }.toList()
        val matches = positions.mapNotNull { SPECIFIER.matchAt(format, it) }
        val failure = positions.getOrNull(matches.size)
        val isTailValid = failure == null || (!isFull && INCOMPLETE_SPECIFIER.matchAt(format, failure) != null)
        val areValid = matches.all { match ->
            val conversion = match.groupValues[6].single()
            if (match.groupValues[5].isEmpty()) conversion in CONVERSIONS else conversion in DATE_CONVERSIONS
        }
        val indexed = matches.filter { it.groupValues[6] != "%" && it.groupValues[6] != "n" }
        return indexed.formatIndexCount().takeIf { isTailValid && areValid }
    }
}

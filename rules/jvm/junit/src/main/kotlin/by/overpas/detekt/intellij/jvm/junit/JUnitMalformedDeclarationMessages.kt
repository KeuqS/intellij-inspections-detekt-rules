package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.name.ClassId

internal data class SignatureProblems(
    val modifiers: List<String>,
    val type: String?,
    val parameters: List<String>,
)

internal fun SignatureProblems.isEmpty(): Boolean =
    modifiers.isEmpty() && type == null && parameters.isEmpty()

internal fun SignatureProblems.message(
    subject: String,
    annotation: ClassId,
): String {
    val beParts = modifiers + listOfNotNull(type?.let { "of type '$it'" })
    val clauses = beParts.mapIndexed { index, part -> if (index == 0) "be $part" else part } + listOfNotNull(
            parameterClause(),
        )
    return "$subject annotated with ${annotation.quoted()} should ${clauses.joinWithAnd()}"
}

private fun SignatureProblems.parameterClause(): String? =
    when (parameters.size) {
        0 -> null
        1 -> "not declare parameter '${parameters.single()}'"
        else -> "not declare parameters ${parameters.dropLast(1).joinToString { "'$it'" }} and '${parameters.last()}'"
    }

private fun List<String>.joinWithAnd(): String =
    if (size <= 1) joinToString() else "${dropLast(1).joinToString()} and ${last()}"

internal fun ClassId.quoted(): String =
    "'@${shortClassName.asString()}'"

internal fun suspendMessage(
    subject: String,
    annotation: ClassId?,
): String =
    if (annotation == null) {
        "$subject should not be a suspending function"
    } else {
        "$subject annotated with ${annotation.quoted()} should not be a suspending function"
    }

internal fun combinationMessage(
    first: List<ClassId>,
    last: ClassId,
): String =
    "Suspicious combination of ${first.joinToString { it.quoted() }} and ${last.quoted()}"

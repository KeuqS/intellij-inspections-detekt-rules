package by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration

import org.jetbrains.kotlin.name.ClassId

internal fun ClassId.signatureMessageOrNull(
    subject: String,
    problems: SignatureProblems,
): String? =
    if (problems.isEmpty()) {
        null
    } else {
        "$subject annotated with ${quoted()} should ${problems.clauses().joinWithAnd()}"
    }

internal fun ClassId.quoted(): String =
    "'@${shortClassName.asString()}'"

internal fun ClassId?.suspendMessage(subject: String): String =
    if (this == null) {
        "$subject should not be a suspending function"
    } else {
        "$subject annotated with ${quoted()} should not be a suspending function"
    }

internal fun combinationMessage(
    first: List<ClassId>,
    last: ClassId,
): String =
    "Suspicious combination of ${first.joinToString { it.quoted() }} and ${last.quoted()}"

private fun List<String>.joinWithAnd(): String =
    if (size <= 1) joinToString() else "${dropLast(1).joinToString()} and ${last()}"

package by.overpas.detekt.intellij.jvm.junit

import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.CSV_FILE_SOURCE
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.EMPTY_SOURCE
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.ENUM_SOURCE
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.FIELD_SOURCE
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.METHOD_SOURCE
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.NULL_AND_EMPTY_SOURCE
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.NULL_SOURCE
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.VALUE_SOURCE
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.annotations.KaAnnotationValue
import org.jetbrains.kotlin.psi.KtNamedFunction

private val NON_COMBINED_TESTS = listOf(JUPITER_TEST, JUPITER_TEST_FACTORY, JUPITER_REPEATED_TEST, JUPITER_PARAMETERIZED_TEST)

private val PARAMETERIZED_SOURCES = listOf(
    METHOD_SOURCE,
    FIELD_SOURCE,
    VALUE_SOURCE,
    ENUM_SOURCE,
    CSV_FILE_SOURCE,
    NULL_SOURCE,
    EMPTY_SOURCE,
    NULL_AND_EMPTY_SOURCE,
)

context(session: KaSession)
internal fun KtNamedFunction.combinationMessage(): String? =
    with(session) {
        val functionSymbol = symbol
        val tests = NON_COMBINED_TESTS.filter { functionSymbol.hasMetaAnnotation(it) }
        val test = tests.singleOrNull()?.takeIf { it != JUPITER_PARAMETERIZED_TEST }
        val sources = if (test == null) emptyList() else PARAMETERIZED_SOURCES.filter { functionSymbol.hasMetaAnnotation(it) }
        when {
            tests.size > 1 -> combinationMessage(tests.dropLast(1), tests.last())
            test != null && sources.isNotEmpty() -> combinationMessage(sources, test)
            else -> null
        }
    }

context(session: KaSession)
internal fun KtNamedFunction.repetitionMalformation(): Malformation? =
    with(session) {
        val annotation = symbol.annotations.firstOrNull { it.classId == JUPITER_REPEATED_TEST }
        val value = annotation?.run { arguments.firstOrNull { it.name.asString() == "value" }?.expression }
        val count = (value as? KaAnnotationValue.ConstantValue)?.run { this.value.value } as? Int
        val call = annotation?.psi
        val anchor = call?.run { valueArguments.firstOrNull()?.getArgumentExpression() ?: this } ?: this@repetitionMalformation
        Malformation(anchor, "The number of repetitions must be greater than zero").takeIf { count != null && count <= 0 }
    }

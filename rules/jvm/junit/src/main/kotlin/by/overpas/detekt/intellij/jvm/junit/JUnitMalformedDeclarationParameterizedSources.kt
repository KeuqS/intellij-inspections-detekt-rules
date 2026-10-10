package by.overpas.detekt.intellij.jvm.junit

import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.ARGUMENTS_SOURCE
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.ARGUMENTS_SOURCES
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.CSV_FILE_SOURCE
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.CSV_SOURCE
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.EMPTY_SOURCE
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.ENUM_SOURCE
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.FIELD_SOURCE
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.METHOD_SOURCE
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.NULL_AND_EMPTY_SOURCE
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.NULL_SOURCE
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.TEST_INFO
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.TEST_REPORTER
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.VALUE_SOURCE
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.annotations.KaAnnotated
import org.jetbrains.kotlin.analysis.api.annotations.KaAnnotation
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter

private val SINGLE_PARAMETER_SOURCES = listOf(
    ENUM_SOURCE,
    VALUE_SOURCE,
    NULL_SOURCE,
    EMPTY_SOURCE,
    NULL_AND_EMPTY_SOURCE,
)

private val MULTIPLE_PARAMETER_SOURCES = listOf(METHOD_SOURCE, FIELD_SOURCE, CSV_FILE_SOURCE, CSV_SOURCE)

private val KNOWN_SOURCES = SINGLE_PARAMETER_SOURCES + MULTIPLE_PARAMETER_SOURCES

internal data class ProvidedParameters(
    val parameters: List<KtParameter>,
    val isMismatch: Boolean,
)

context(session: KaSession)
internal fun KaAnnotated.hasArgumentsSource(): Boolean =
    hasMetaAnnotation(ARGUMENTS_SOURCE) || annotations.any { it.classId == ARGUMENTS_SOURCES }

internal fun KaAnnotated.knownSources(): List<KaAnnotation> =
    annotations.filter { it.classId in KNOWN_SOURCES }

context(session: KaSession)
internal fun KtNamedFunction.providedParameters(settings: MalformedSettings): ProvidedParameters =
    with(session) {
        val parameters = valueParameters.filterNot { parameter ->
            val type = parameter.symbol.returnType
            parameter.isIgnorable(settings) ||
                parameter.symbol.hasParameterResolver() ||
                type.isSubtypeOf(TEST_INFO) ||
                type.isSubtypeOf(TEST_REPORTER)
        }
        ProvidedParameters(parameters, parameters.size > 1 && !inParameterResolverContext())
    }

context(session: KaSession)
internal fun KaAnnotation.isCustomSource(): Boolean =
    with(session) {
        when (val id = classId) {
            null -> false
            ARGUMENTS_SOURCE -> true
            ARGUMENTS_SOURCES -> arguments.flatMap { it.expression.flatten() }.isNotEmpty()
            in KNOWN_SOURCES -> false
            else -> findClass(id)?.hasArgumentsSource() == true
        }
    }

internal fun sourceConflictMessage(
    sources: List<KaAnnotation>,
    provided: ProvidedParameters,
    hasCustomSource: Boolean,
): String? {
    val single = sources.firstOrNull { it.classId in SINGLE_PARAMETER_SOURCES }?.classId
    return when {
        sources.any { it.classId in MULTIPLE_PARAMETER_SOURCES } -> null
        single == null && !hasCustomSource -> "No sources are provided, the suite would be empty"
        single != null && provided.isMismatch -> "Only a single parameter can be provided by ${single.quoted()}"
        else -> null
    }
}

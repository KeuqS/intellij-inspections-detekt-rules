package by.overpas.detekt.intellij.jvm.junit

import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.EMPTY_SOURCE
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.METHOD_SOURCE
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.NULL_AND_EMPTY_SOURCE
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.NULL_SOURCE
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.VALUE_SOURCE
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.annotations.KaAnnotation
import org.jetbrains.kotlin.psi.KtNamedFunction

context(session: KaSession)
internal fun KtNamedFunction.parameterizedMalformations(settings: MalformedSettings): List<Malformation> =
    with(session) {
        val functionSymbol = symbol
        if (functionSymbol.hasMetaAnnotation(JUPITER_PARAMETERIZED_TEST)) {
            val sources = functionSymbol.knownSources()
            val provided = providedParameters(settings)
            val hasCustomSource = functionSymbol.annotations.any { it.isCustomSource() }
            listOfNotNull(sourceConflictMessage(sources, provided, hasCustomSource)?.let { malformation(it) }) +
                sources.flatMap { sourceMalformations(it, provided) }
        } else {
            emptyList()
        }
    }

context(session: KaSession)
private fun KtNamedFunction.sourceMalformations(annotation: KaAnnotation, provided: ProvidedParameters): List<Malformation> {
    val anchor = annotation.psi ?: this
    val messages = when (annotation.classId) {
        VALUE_SOURCE -> listOfNotNull(annotation.valueSourceMessage())
        NULL_SOURCE -> nullSourceMessages(provided)
        EMPTY_SOURCE, NULL_AND_EMPTY_SOURCE -> listOfNotNull(annotation.emptySourceMessage(provided))
        else -> emptyList()
    }
    val methodSource = if (annotation.classId == METHOD_SOURCE) methodSourceMalformations(annotation, provided) else emptyList()
    return messages.map { Malformation(anchor, it) } + methodSource
}

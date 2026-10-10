package by.overpas.detekt.intellij.jvm.junit

import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.EMPTY_SOURCE
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.METHOD_SOURCE
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.NULL_AND_EMPTY_SOURCE
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.NULL_SOURCE
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.VALUE_SOURCE
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.annotations.KaAnnotation
import org.jetbrains.kotlin.psi.KtNamedFunction

context(session: KaSession, settings: MalformedSettings)
internal fun KtNamedFunction.parameterizedMalformations(): List<Malformation> =
    with(session) {
        val functionSymbol = symbol
        if (functionSymbol.hasMetaAnnotation(JUPITER_PARAMETERIZED_TEST)) {
            val sources = functionSymbol.knownSources()
            val provided = providedParameters()
            val hasCustomSource = functionSymbol.annotations.any { it.isCustomSource() }
            listOfNotNull(sourceConflictMessage(sources, provided, hasCustomSource)?.let { malformation(it) }) +
                context(provided) { sources.flatMap { sourceMalformations(it) } }
        } else {
            emptyList()
        }
    }

context(session: KaSession, provided: ProvidedParameters)
private fun KtNamedFunction.sourceMalformations(annotation: KaAnnotation): List<Malformation> {
    val anchor = annotation.psi ?: this
    val messages = when (annotation.classId) {
        VALUE_SOURCE -> listOfNotNull(annotation.valueSourceMessage())
        NULL_SOURCE -> nullSourceMessages()
        EMPTY_SOURCE, NULL_AND_EMPTY_SOURCE -> listOfNotNull(annotation.emptySourceMessage())
        else -> emptyList()
    }
    val methodSource = if (annotation.classId == METHOD_SOURCE) {
        annotation.methodSourceMalformations(this)
    } else {
        emptyList()
    }
    return messages.map { Malformation(anchor, it) } + methodSource
}

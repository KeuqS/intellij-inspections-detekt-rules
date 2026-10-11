package by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration

import by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration.MalformedClassIds.NULL_SOURCE
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.annotations.KaAnnotation

internal fun KaAnnotation.valueSourceMessage(): String? =
    when (psi?.run { valueArguments.size }) {
        0 -> "No value source is defined"
        null, 1 -> null
        else -> "Exactly one type of input must be provided"
    }

context(provided: ProvidedParameters)
internal fun nullSourceMessages(): List<String> =
    listOfNotNull(
        singleParameterMessage(NULL_SOURCE.quoted()).takeIf { provided.isMismatch },
        noParametersMessage(NULL_SOURCE.quoted()).takeIf { provided.parameters.isEmpty() },
    )

context(session: KaSession, provided: ProvidedParameters)
internal fun KaAnnotation.emptySourceMessage(): String? =
    with(session) {
        val name = classId?.quoted().orEmpty()
        val type = provided.parameters.firstOrNull()?.run { symbol.returnType }
        when {
            provided.isMismatch -> singleParameterMessage(name)

            type == null -> noParametersMessage(name)

            type.isEmptySourceType() -> null

            else ->
                "$name cannot provide an argument to method because method has an unsupported parameter of " +
                    "'${type.expandedSymbol?.name?.asString().orEmpty()}' type"
        }
    }

private fun singleParameterMessage(annotation: String): String =
    "Only a single parameter can be provided by $annotation"

private fun noParametersMessage(annotation: String): String =
    "$annotation cannot provide an argument to method because method doesn't have parameters"

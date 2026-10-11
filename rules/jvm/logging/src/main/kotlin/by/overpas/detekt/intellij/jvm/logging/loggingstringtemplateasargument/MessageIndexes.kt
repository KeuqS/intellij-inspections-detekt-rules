package by.overpas.detekt.intellij.jvm.logging.loggingstringtemplateasargument

import by.overpas.detekt.intellij.jvm.logging.classIdOrNull
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.resolution.successfulFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.psi.KtCallExpression

private val TEXT_CLASSES = setOf(StandardClassIds.String, StandardClassIds.CharSequence)

context(session: KaSession)
internal fun KtCallExpression.messageIndex(argumentCount: Int): Int? {
    val parameters = with(session) { resolveToCall()?.successfulFunctionCallOrNull() }
        ?.run { symbol.valueParameters }
        .orEmpty()
    val textIndices = parameters.indices.filter { parameters[it].returnType.classIdOrNull in TEXT_CLASSES }
    return when {
        argumentCount == 0 -> null
        0 in textIndices -> 0
        argumentCount >= 2 && 1 in textIndices -> 1
        else -> null
    }
}

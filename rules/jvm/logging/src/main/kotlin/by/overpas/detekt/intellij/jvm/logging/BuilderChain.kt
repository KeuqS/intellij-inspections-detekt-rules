package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression

private const val MAX_BUILDER_LENGTH = 20
private const val ADD_ARGUMENT = "addArgument"
private const val SET_MESSAGE = "setMessage"
private val BUILDER_CHAIN = setOf("addKeyValue", "addMarker", "setCause")

internal class BuilderChain(private val isSlf4jBuilder: Boolean) {

    context(session: KaSession)
    fun messageSetterArgument(call: KtCallExpression): KtExpression? {
        if (!isSlf4jBuilder) return null
        val setter = generateSequence(call.previousInChain) { it.previousInChain }
            .take(MAX_BUILDER_LENGTH)
            .firstOrNull { it.calleeName !in BUILDER_CHAIN && it.calleeName != ADD_ARGUMENT }
            ?.takeIf { it.calleeName == SET_MESSAGE && it.valueArguments.size == 1 }
        val argument = setter?.run { valueArguments.single().getArgumentExpression() }
        return argument?.takeIf { with(session) { it.expressionType }?.classIdOrNull == StandardClassIds.String }
    }

    context(session: KaSession)
    fun additionalArguments(
        call: KtCallExpression,
        isIntermediateMessageAllowed: Boolean,
    ): List<KtExpression>? {
        if (!isSlf4jBuilder) return emptyList()
        val links = generateSequence(call.previousInChain) { it.previousInChain }.take(MAX_BUILDER_LENGTH).toList()
        val end = links.indexOfFirst { link ->
            val name = link.calleeName
            name !in BUILDER_CHAIN && name != ADD_ARGUMENT && !(isIntermediateMessageAllowed && name == SET_MESSAGE)
        }
        val terminal = links.getOrNull(end)
        val isLevelTerminal = terminal?.calleeName.orEmpty().startsWith("at") && terminal?.loggerLevel() != null
        val arguments = links.asSequence()
            .take(end.coerceAtLeast(0))
            .filter { it.calleeName == ADD_ARGUMENT }
            .map { it.valueArguments.firstOrNull()?.getArgumentExpression() }
            .toList()
        return arguments.filterNotNull().takeIf { isLevelTerminal && it.size == arguments.size }
    }
}

package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaValueParameterSymbol
import org.jetbrains.kotlin.psi.KtCallExpression

internal class PlaceholderContextSources(
    private val call: KtCallExpression,
    private val loggerType: PlaceholderLoggerType,
    private val builderChain: BuilderChain,
) {

    context(session: KaSession)
    fun ofBuilder(): PlaceholderContext? {
        val message = builderChain.messageSetterArgument(call)
        val additional = builderChain.additionalArguments(call, isIntermediateMessageAllowed = true)
        val parts = message?.stringParts()
        return if (message == null || additional == null || parts == null) {
            null
        } else {
            PlaceholderContext(
                placeholderArguments = additional.sortedBy { it.textRange.startOffset },
                logStringArgument = message,
                parts = parts,
                loggerType = loggerType,
                isLastArgumentException = false,
                isLastArgumentSupplier = false,
            )
        }
    }

    context(session: KaSession)
    fun ofArguments(parameters: List<KaValueParameterSymbol>): PlaceholderContext? {
        val arguments = call.valueArguments.mapNotNull { it.getArgumentExpression() }
        val index = parameters.logStringIndex()?.takeIf { it <= arguments.size } ?: return null
        val placeholderArguments = arguments.subList(index, arguments.size)
        val isArray = with(session) { placeholderArguments.singleOrNull()?.expressionType?.isArrayOrPrimitiveArray }
        val additional = builderChain.additionalArguments(call, isIntermediateMessageAllowed = false)
        val parts = arguments[index - 1].stringParts()
        val lastArgument = arguments.last()
        val isArrayOnly = isArray == true && parameters.size > 1
        return if (isArrayOnly || additional == null || parts == null) {
            null
        } else {
            PlaceholderContext(
                placeholderArguments = (placeholderArguments + additional).sortedBy { it.textRange.startOffset },
                logStringArgument = arguments[index - 1],
                parts = parts,
                loggerType = loggerType,
                isLastArgumentException = lastArgument.isThrowable(),
                isLastArgumentSupplier = lastArgument.couldBeThrowableSupplier(loggerType, parameters.last()),
            )
        }
    }
}

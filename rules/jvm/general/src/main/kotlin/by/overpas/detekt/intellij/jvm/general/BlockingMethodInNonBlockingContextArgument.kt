package by.overpas.detekt.intellij.jvm.general

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.resolution.KaFunctionCall
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaCallableSymbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name

private val COROUTINE_CONTEXT_PLUS = CallableId(
    ClassId.fromString("kotlin/coroutines/CoroutineContext"),
    Name.identifier("plus"),
)

internal class BlockingMethodInNonBlockingContextArgument(private val call: KaFunctionCall<*>) {

    context(session: KaSession)
    fun dispatcher(): BlockingMethodInNonBlockingContextDispatcher {
        val firstParameter = call.symbol.valueParameters.firstOrNull()
        val argument = call.valueArgumentMapping.entries.firstOrNull { it.value.symbol == firstParameter }?.key
        val symbols = BlockingMethodInNonBlockingContextSymbols()
        val symbol = argument?.let { symbols.resolve(it) }
            ?: return BlockingMethodInNonBlockingContextDispatcher.NO_INFORMATION
        val dispatcher = symbols.dispatcherOf(symbol)
        return when {
            dispatcher != BlockingMethodInNonBlockingContextDispatcher.UNKNOWN -> dispatcher
            isCoroutineContextPlus(symbol) -> BlockingMethodInNonBlockingContextNested(argument).dispatcher()
            else -> BlockingMethodInNonBlockingContextDispatcher.UNKNOWN
        }
    }

    context(session: KaSession)
    private fun isCoroutineContextPlus(symbol: KaCallableSymbol): Boolean =
        with(session) {
            symbol.callableId?.callableName == COROUTINE_CONTEXT_PLUS.callableName &&
                (sequenceOf(symbol) + symbol.allOverriddenSymbols).any { it.callableId == COROUTINE_CONTEXT_PLUS }
        }
}

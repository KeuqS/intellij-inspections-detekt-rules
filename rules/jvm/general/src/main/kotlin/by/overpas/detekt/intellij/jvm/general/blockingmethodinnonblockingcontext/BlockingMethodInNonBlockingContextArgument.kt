package by.overpas.detekt.intellij.jvm.general.blockingmethodinnonblockingcontext

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.resolution.KaFunctionCall
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaCallableSymbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name

private val COROUTINE_CONTEXT = ClassId.fromString("kotlin/coroutines/CoroutineContext")

private val COROUTINE_CONTEXT_PLUS = CallableId(COROUTINE_CONTEXT, Name.identifier("plus"))

internal class BlockingMethodInNonBlockingContextArgument(private val call: KaFunctionCall<*>) {

    context(session: KaSession)
    fun dispatcher(): BlockingMethodInNonBlockingContextDispatcher {
        val firstParameter = call.symbol.valueParameters.firstOrNull()
        val argument = call.valueArgumentMapping.entries.firstOrNull { it.value.symbol == firstParameter }?.key
        val symbols = BlockingMethodInNonBlockingContextSymbols()
        val symbol = argument?.let { symbols.resolve(it) }
        val isContext = argument != null &&
            with(session) { firstParameter?.run { returnType.isSubtypeOf(COROUTINE_CONTEXT) } == true }
        val unresolved = if (isContext) {
            BlockingMethodInNonBlockingContextDispatcher.UNKNOWN
        } else {
            BlockingMethodInNonBlockingContextDispatcher.NO_INFORMATION
        }
        val dispatcher = symbol?.let { symbols.dispatcherOf(it) } ?: unresolved
        return when {
            argument == null || symbol == null -> dispatcher
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

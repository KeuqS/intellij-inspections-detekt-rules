package by.overpas.detekt.intellij.jvm.general

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.resolution.KaCallableMemberCall
import org.jetbrains.kotlin.analysis.api.resolution.successfulCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaCallableSymbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtExpression

private val BLOCKING_EXECUTOR =
    BlockingMethodInNonBlockingContextAnnotatedType(ClassId.fromString("org/jetbrains/annotations/BlockingExecutor"))

private val NON_BLOCKING_EXECUTOR =
    BlockingMethodInNonBlockingContextAnnotatedType(ClassId.fromString("org/jetbrains/annotations/NonBlockingExecutor"))

private val IO_DISPATCHER = FqName("kotlinx.coroutines.Dispatchers.IO")

private val NON_BLOCKING_DISPATCHERS = setOf(
    FqName("kotlinx.coroutines.Dispatchers.Main"),
    FqName("kotlinx.coroutines.Dispatchers.Default"),
)

internal class BlockingMethodInNonBlockingContextSymbols {

    context(session: KaSession)
    fun resolve(expression: KtExpression): KaCallableSymbol? =
        with(session) { expression.resolveToCall()?.successfulCallOrNull<KaCallableMemberCall<*, *>>()?.symbol }

    context(session: KaSession)
    fun dispatcherOf(symbol: KaCallableSymbol): BlockingMethodInNonBlockingContextDispatcher {
        val name = symbol.callableId?.asSingleFqName()
        return when {
            BLOCKING_EXECUTOR.matches(symbol.returnType) ->
                BlockingMethodInNonBlockingContextDispatcher.BLOCKING

            NON_BLOCKING_EXECUTOR.matches(symbol.returnType) ->
                BlockingMethodInNonBlockingContextDispatcher.NON_BLOCKING

            name == IO_DISPATCHER -> BlockingMethodInNonBlockingContextDispatcher.BLOCKING

            name in NON_BLOCKING_DISPATCHERS -> BlockingMethodInNonBlockingContextDispatcher.NON_BLOCKING

            else -> BlockingMethodInNonBlockingContextDispatcher.UNKNOWN
        }
    }
}

package by.overpas.detekt.intellij.jvm.general.blockingmethodinnonblockingcontext

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.resolution.KaImplicitReceiverValue
import org.jetbrains.kotlin.analysis.api.resolution.successfulFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaPropertySymbol
import org.jetbrains.kotlin.analysis.api.types.KaClassType
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtProperty

private val COROUTINE_SCOPE = ClassId.fromString("kotlinx/coroutines/CoroutineScope")

private val COROUTINE_CONTEXT = ClassId.fromString("kotlin/coroutines/CoroutineContext")

internal class BlockingMethodInNonBlockingContextScope(private val callExpression: KtExpression) {

    context(session: KaSession)
    fun dispatcher(): BlockingMethodInNonBlockingContextDispatcher =
        with(session) {
            val call = callExpression.resolveToCall()?.successfulFunctionCallOrNull()
            val receiver = (call?.run { dispatchReceiver ?: extensionReceiver } as? KaImplicitReceiverValue)
                ?.run { type.takeIf { it.isSubtypeOf(COROUTINE_SCOPE) } }
            val property = ((receiver as? KaClassType)?.symbol as? KaClassSymbol)?.run {
                memberScope.callables.filterIsInstance<KaPropertySymbol>()
                    .singleOrNull { it.returnType.isSubtypeOf(COROUTINE_CONTEXT) }
            }
            val initializer = (property?.psi as? KtProperty)?.initializer
            when {
                receiver == null -> BlockingMethodInNonBlockingContextDispatcher.NO_INFORMATION
                initializer == null -> BlockingMethodInNonBlockingContextDispatcher.UNKNOWN
                else -> BlockingMethodInNonBlockingContextNested(initializer).dispatcher()
            }
        }
}

package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaFunctionSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaVariableSymbol
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPsiUtil
import org.jetbrains.kotlin.psi.KtQualifiedExpression

private const val MAX_SOURCE_DEPTH = 10

context(session: KaSession)
internal fun KtExpression.loggerSource(): KaSymbol? =
    generateSequence(loggerReceiverSymbol()) { symbol ->
        val isBuilder = symbol is KaVariableSymbol && symbol.returnType.classIdOrNull in BUILDER_CLASSES
        val initializer = (symbol.psi as? KtProperty)?.initializer as? KtQualifiedExpression
        (initializer?.selectorExpression as? KtCallExpression)?.takeIf { isBuilder }?.loggerReceiverSymbol()
    }
        .take(MAX_SOURCE_DEPTH)
        .lastOrNull()
        ?.takeIf { symbol ->
            when (symbol) {
                is KaFunctionSymbol -> symbol.valueParameters.isEmpty() &&
                    symbol.returnType.classIdOrNull in LOGGER_CLASSES

                is KaVariableSymbol -> symbol.returnType.classIdOrNull in LOGGER_CLASSES

                else -> false
            }
        }

context(session: KaSession)
private fun KtExpression.loggerReceiverSymbol(): KaSymbol? {
    val receiver = callReceiver?.let(KtPsiUtil::safeDeparenthesize)
    val unwrappedCall = if (receiver is KtCallExpression) receiver.callReceiver else receiver
    val unwrapped = if (unwrappedCall is KtQualifiedExpression) unwrappedCall.receiverExpression else unwrappedCall
    return (unwrapped as? KtNameReferenceExpression)?.resolvedSymbol()
}

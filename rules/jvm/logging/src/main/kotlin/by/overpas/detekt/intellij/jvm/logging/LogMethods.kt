package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.resolution.successfulFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.psi.KtCallExpression

private val LOG_METHOD_NAMES = LogMethod.entries.flatMap { it.names }.toSet()

context(session: KaSession)
internal fun KtCallExpression.logMethod(): LogMethod? {
    if (calleeName !in LOG_METHOD_NAMES) return null
    val call = with(session) { resolveToCall()?.successfulFunctionCallOrNull() }
    val name = call?.run { symbol.callableId }?.run { callableName.asString() }
    val receiverType = call?.dispatchReceiver?.type
    return LogMethod.entries.firstOrNull { method ->
        name in method.names && receiverType != null && with(session) { receiverType.isSubtypeOf(method.classId) }
    }
}

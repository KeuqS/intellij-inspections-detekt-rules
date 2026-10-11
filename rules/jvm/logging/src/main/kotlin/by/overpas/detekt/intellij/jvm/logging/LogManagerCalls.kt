package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.resolution.successfulFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression

context(session: KaSession)
internal fun KtExpression?.isFormatterLoggerCall(): Boolean {
    val call = (this as? KtCallExpression)?.let { with(session) { it.resolveToCall()?.successfulFunctionCallOrNull() } }
    val callableId = call?.run { symbol.callableId }
    return callableId != null &&
        callableId.classId == LOG4J_LOG_MANAGER &&
        callableId.callableName.asString() == "getFormatterLogger"
}

package by.overpas.detekt.intellij.jvm.logging.loggingplaceholdercountmatchesargumentcount

import by.overpas.detekt.intellij.jvm.logging.JAVA_SUPPLIER
import by.overpas.detekt.intellij.jvm.logging.LOG4J_SUPPLIER
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaValueParameterSymbol
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression

private val SUPPLIER_LOGGER_TYPES = setOf(
    PlaceholderLoggerType.LOG4J_OLD_STYLE,
    PlaceholderLoggerType.LOG4J_FORMATTED_STYLE,
)

context(session: KaSession)
internal fun KtExpression.couldBeThrowableSupplier(
    loggerType: PlaceholderLoggerType,
    lastParameter: KaValueParameterSymbol,
): Boolean {
    val parameterType = lastParameter.returnType
    val isSupplier = loggerType in SUPPLIER_LOGGER_TYPES &&
        with(session) { parameterType.isSubtypeOf(JAVA_SUPPLIER) || parameterType.isSubtypeOf(LOG4J_SUPPLIER) }
    return isSupplier &&
        if (this is KtLambdaExpression) {
            returnedExpressions.all { expression ->
                with(session) { expression.expressionType }?.isCastableToThrowable() != false
            }
        } else {
            with(session) { expressionType }?.functionalReturnType()?.isCastableToThrowable() != false
        }
}

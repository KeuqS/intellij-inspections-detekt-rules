package by.overpas.detekt.intellij.jvm.logging.loggingsimilarmessage

import by.overpas.detekt.intellij.jvm.logging.LevelType
import by.overpas.detekt.intellij.jvm.logging.LogMethod
import by.overpas.detekt.intellij.jvm.logging.LogMethodKind
import by.overpas.detekt.intellij.jvm.logging.calleeName
import by.overpas.detekt.intellij.jvm.logging.isThrowable
import by.overpas.detekt.intellij.jvm.logging.loggerLevel
import by.overpas.detekt.intellij.jvm.logging.previousInChain
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtCallExpression

private const val MAX_BUILDER_LENGTH = 20
private val THROWABLE_SETTERS = setOf("withThrowable", "setCause")

internal object SkippedLogCalls {

    context(session: KaSession)
    fun isSkipped(
        call: KtCallExpression,
        method: LogMethod,
    ): Boolean {
        val isBuilder = method.kind == LogMethodKind.BUILDER
        val hasThrowableSetter = generateSequence(call.previousInChain) { it.previousInChain }
            .take(MAX_BUILDER_LENGTH)
            .any { it.calleeName in THROWABLE_SETTERS }
        val lastArgument = call.valueArguments.lastOrNull()?.getArgumentExpression()
        return (isBuilder && hasThrowableSetter) ||
            (method == LogMethod.IDEA && call.loggerLevel() == LevelType.ERROR.name) ||
            (!isBuilder && lastArgument.isThrowable())
    }
}

package by.overpas.detekt.intellij.jvm.logging.loggingplaceholdercountmatchesargumentcount

import by.overpas.detekt.intellij.jvm.logging.LogMethod
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtCallExpression

context(session: KaSession)
internal fun KtCallExpression.placeholderLoggerType(
    method: LogMethod,
    hasSlf4jBridge: Boolean,
): PlaceholderLoggerType? =
    when (method) {
        LogMethod.SLF4J -> if (hasSlf4jBridge) PlaceholderLoggerType.LOG4J_OLD_STYLE else PlaceholderLoggerType.SLF4J

        LogMethod.SLF4J_BUILDER ->
            if (hasSlf4jBridge) PlaceholderLoggerType.SLF4J_EQUAL_PLACEHOLDERS else PlaceholderLoggerType.SLF4J

        LogMethod.LOG4J -> Log4jStyles.loggerStyle(this)

        LogMethod.LOG4J_BUILDER -> Log4jBuilderStyles.chainStyle(this)

        LogMethod.AKKA -> PlaceholderLoggerType.AKKA_PLACEHOLDERS

        LogMethod.LEGACY_LOG4J,
        LogMethod.LEGACY_CATEGORY,
        LogMethod.LEGACY_COMMONS,
        LogMethod.LEGACY_JAVA,
        LogMethod.IDEA,
        -> null
    }

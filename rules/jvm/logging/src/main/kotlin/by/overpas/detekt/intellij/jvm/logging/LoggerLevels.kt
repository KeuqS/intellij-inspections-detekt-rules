package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtQualifiedExpression

private const val LOG = "log"
private val LEGACY_METHODS_WITH_LEVEL = setOf(LOG, "l7dlog", "logp", "logrb")

context(session: KaSession)
internal fun KtCallExpression.loggerLevel(isLog: Boolean = false): String? {
    val name = calleeName
    return when {
        name == null -> null

        !isLog && name != LOG -> LevelNames.ACTUAL.byName(name)

        else -> levelByFirstArgument(LEVEL_CLASSES, LevelNames.ACTUAL) ?: levelSourceCall()?.let { next ->
            next.takeIf { it.calleeName == "atLevel" }?.levelByFirstArgument(LEVEL_CLASSES, LevelNames.ACTUAL)
                ?: next.loggerLevel(isLog = true)
                ?: next.calleeName?.let(LevelNames.ACTUAL::byName)
        }
    }
}

context(session: KaSession)
internal fun KtCallExpression.legacyLoggerLevel(): String? {
    val name = calleeName ?: return null
    val argumentLevel = if (name in LEGACY_METHODS_WITH_LEVEL) {
        levelByFirstArgument(LEGACY_LEVEL_CLASSES, LevelNames.LEGACY)
    } else {
        null
    }
    return argumentLevel ?: LevelNames.LEGACY.byName(name)
}

context(session: KaSession)
private fun KtCallExpression.levelSourceCall(): KtCallExpression? {
    val source = when (val receiver = callReceiver) {
        null -> null

        is KtQualifiedExpression -> receiver.selectorExpression

        is KtNameReferenceExpression ->
            ((receiver.resolvedSymbol()?.psi as? KtProperty)?.initializer as? KtQualifiedExpression)
                ?.selectorExpression

        else -> receiver
    }
    return source as? KtCallExpression
}

package by.overpas.detekt.intellij.jvm.logging.loggingconditiondisagreeswithloglevelstatement

import by.overpas.detekt.intellij.jvm.logging.LogMethodKind
import by.overpas.detekt.intellij.jvm.logging.conditionLevel
import by.overpas.detekt.intellij.jvm.logging.guardCondition
import by.overpas.detekt.intellij.jvm.logging.guardedLogCalls
import by.overpas.detekt.intellij.jvm.logging.logMethod
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression

context(session: KaSession)
internal fun KtCallExpression.levelDisagreement(): LevelDisagreement? {
    val scheme = when (logMethod()?.kind) {
        LogMethodKind.LOGGER, LogMethodKind.BUILDER -> ActualLevelScheme
        LogMethodKind.LEGACY -> LegacyLevelScheme
        LogMethodKind.OTHER, null -> null
    }
    val guard = scheme?.let { guardCondition() }
    if (scheme == null || guard == null || guard.isUsedByLogCall(scheme)) return null
    val callLevel = scheme.ofCall(this)
    val conditionLevel = scheme.ofCondition(guard)
    val isDisagreement = callLevel != null && conditionLevel != null && callLevel != conditionLevel
    return if (isDisagreement) LevelDisagreement(guard, conditionLevel, callLevel) else null
}

context(session: KaSession)
private fun KtExpression.isUsedByLogCall(scheme: LevelScheme): Boolean {
    val conditionLevel = scheme.ofCondition(this) ?: return false
    return guardedLogCalls().any { it.guardCondition() == this && scheme.ofCall(it) == conditionLevel }
}

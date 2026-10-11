package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtCallExpression

context(session: KaSession)
internal fun KtCallExpression.isSurroundedByLogGuard(): Boolean {
    val guard = guardCondition() ?: return false
    val level = loggerLevel()
    val conditionLevel = guard.conditionLevel()
    return level == null || conditionLevel == null || level == conditionLevel
}

context(session: KaSession)
internal fun KtCallExpression.isGuardedAtSameLevel(): Boolean {
    val level = loggerLevel() ?: return false
    return guardCondition()?.conditionLevel() == level
}

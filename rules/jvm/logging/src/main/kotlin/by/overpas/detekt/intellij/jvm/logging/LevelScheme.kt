package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression

internal interface LevelScheme {

    context(session: KaSession)
    fun ofCall(call: KtCallExpression): String?

    context(session: KaSession)
    fun ofCondition(guard: KtExpression): String?
}

internal object ActualLevelScheme : LevelScheme {

    context(session: KaSession)
    override fun ofCall(call: KtCallExpression): String? =
        call.loggerLevel()

    context(session: KaSession)
    override fun ofCondition(guard: KtExpression): String? =
        guard.conditionLevel()
}

internal object LegacyLevelScheme : LevelScheme {

    context(session: KaSession)
    override fun ofCall(call: KtCallExpression): String? =
        call.legacyLoggerLevel()

    context(session: KaSession)
    override fun ofCondition(guard: KtExpression): String? =
        guard.legacyConditionLevel()
}

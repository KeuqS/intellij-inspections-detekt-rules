package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression

internal enum class ConditionLevels(
    private val levelMethods: Set<String>,
    private val levelClasses: Set<ClassId>,
    private val names: LevelNames,
) {
    ACTUAL(setOf("isEnabled", "isEnabledForLevel"), LEVEL_CLASSES, LevelNames.ACTUAL),
    LEGACY(setOf("isEnabledFor", "isLoggable"), LEGACY_LEVEL_CLASSES, LevelNames.LEGACY),
    ;

    context(session: KaSession)
    fun guardLevel(condition: KtExpression): String? =
        when (condition) {
            is KtCallExpression -> condition.calleeName?.let { name ->
                if (name in levelMethods) condition.levelByFirstArgument(levelClasses, names) else names.byGuard(name)
            }

            is KtNameReferenceExpression -> names.byGuard(condition.getReferencedName())

            else -> null
        }
}

context(session: KaSession)
internal fun KtExpression.conditionLevel(): String? =
    ConditionLevels.ACTUAL.guardLevel(this)

context(session: KaSession)
internal fun KtExpression.legacyConditionLevel(): String? =
    ConditionLevels.LEGACY.guardLevel(this)

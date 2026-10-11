package by.overpas.detekt.intellij.gradle.bestpractices

import org.jetbrains.kotlin.psi.KtCallExpression

internal fun KtCallExpression.kotlinModuleCoordinates(): String? {
    val module = valueArgumentList?.argument("module", 0)?.stringValue()
    val versionSuffix = valueArgumentList?.argument("version", 1).versionSuffix()
    return if (module == null || versionSuffix == null) null else "org.jetbrains.kotlin:kotlin-$module$versionSuffix"
}

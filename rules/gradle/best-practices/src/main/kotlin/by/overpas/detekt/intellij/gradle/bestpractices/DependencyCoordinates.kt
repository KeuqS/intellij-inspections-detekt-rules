package by.overpas.detekt.intellij.gradle.bestpractices

import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtValueArgumentList

internal fun KtCallExpression.dependencyCoordinates(): String? {
    val arguments = valueArgumentList ?: return null
    val single = arguments.arguments.singleOrNull()?.takeUnless { it.isNamed() }?.getArgumentExpression()
    return when {
        single is KtCallExpression && single.calleeExpression?.text == "kotlin" -> single.kotlinModuleCoordinates()
        single != null -> single.stringValue()
        else -> arguments.namedCoordinates()
    }
}

private fun KtValueArgumentList.namedCoordinates(): String? {
    val group = argument("group", 0)?.stringValue()
    val name = argument("name", 1)?.stringValue()
    val versionSuffix = argument("version", 2).versionSuffix()
    return if (group == null || name == null || versionSuffix == null) null else "$group:$name$versionSuffix"
}

package by.overpas.detekt.intellij.gradle.bestpractices.redundantkotlinstdlibdependency

import by.overpas.detekt.intellij.gradle.bestpractices.stringValue
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression

private const val KOTLIN_JVM_PLUGIN = "org.jetbrains.kotlin.jvm"

internal fun KtBlockExpression.kotlinJvmPluginVersion(): String? =
    statements.firstNotNullOfOrNull { it.kotlinJvmVersion() }

private fun KtExpression.kotlinJvmVersion(): String? {
    val operations = generateSequence(this as? KtBinaryExpression) { it.left as? KtBinaryExpression }.toList()
    val plugin = (operations.lastOrNull()?.left ?: this) as? KtCallExpression
    val argument = plugin?.valueArguments?.firstOrNull()?.getArgumentExpression()?.stringValue()
    val pluginId = when (plugin?.calleeExpression?.text) {
        "kotlin" -> argument?.let { "org.jetbrains.kotlin.$it" }
        "id" -> argument
        else -> null
    }
    val isApplied = operations.none {
        it.operationReference.getReferencedName() == "apply" && it.right?.text == "false"
    }
    val version = operations.firstOrNull { it.operationReference.getReferencedName() == "version" }
    return version?.right?.stringValue()?.takeIf { pluginId == KOTLIN_JVM_PLUGIN && isApplied }
}

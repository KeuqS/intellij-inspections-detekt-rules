package by.overpas.detekt.intellij.gradle.bestpractices.avoidapplypluginmethod

import by.overpas.detekt.intellij.IntellijInspection
import by.overpas.detekt.intellij.gradle.bestpractices.isGradleScript
import by.overpas.detekt.intellij.gradle.bestpractices.name
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile

@IntellijInspection("AvoidApplyPluginMethod")
class AvoidApplyPluginMethod(config: Config) :
    Rule(
        config,
        "The `apply(plugin = ...)` and `apply<T>()` calls are the legacy way to apply a plugin. " +
            "Apply the plugin in the `plugins` block.",
    ) {

    override fun visitKtFile(file: KtFile) {
        if (file.isGradleScript()) super.visitKtFile(file)
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        if (expression.calleeExpression?.text != "apply" || expression.lambdaArguments.isNotEmpty()) return
        val arguments = expression.valueArguments
        val isAppliedById = arguments.any { it.name() == "plugin" }
        val isAppliedByType = arguments.isEmpty() && expression.typeArguments.isNotEmpty()
        if (isAppliedById || isAppliedByType) {
            report(Finding(Entity.from(expression), "The 'apply' plugin syntax is older and not recommended"))
        }
    }
}

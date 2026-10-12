package by.overpas.detekt.intellij.gradle.bestpractices.avoiddependencynamedargumentsnotation

import by.overpas.detekt.intellij.IntellijInspection
import by.overpas.detekt.intellij.gradle.bestpractices.enclosingBlockName
import by.overpas.detekt.intellij.gradle.bestpractices.isGradleScript
import by.overpas.detekt.intellij.gradle.bestpractices.name
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtStringTemplateExpression

@IntellijInspection("AvoidDependencyNamedArgumentsNotation")
class AvoidDependencyNamedArgumentsNotation(config: Config) :
    Rule(
        config,
        "A dependency is declared with separate `group`, `name` and `version` arguments. " +
            "Use one `group:name:version` string.",
    ) {

    override fun visitKtFile(file: KtFile) {
        if (file.isGradleScript()) super.visitKtFile(file)
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        if (expression.parent !is KtBlockExpression || expression.enclosingBlockName() != "dependencies") return
        if (expression.calleeExpression?.text == "add") return
        val arguments = expression.valueArgumentList?.arguments.orEmpty()
        val hasGroupArgument = arguments.any { it.name() == "group" }
        val hasPositionalCoordinates = arguments.size >= 2 &&
            arguments.take(2).all { !it.isNamed() && it.getArgumentExpression() is KtStringTemplateExpression }
        if (hasGroupArgument || hasPositionalCoordinates) {
            report(
                Finding(
                    Entity.from(expression.valueArgumentList ?: expression),
                    "GAV string notation should be used instead of named arguments",
                ),
            )
        }
    }
}

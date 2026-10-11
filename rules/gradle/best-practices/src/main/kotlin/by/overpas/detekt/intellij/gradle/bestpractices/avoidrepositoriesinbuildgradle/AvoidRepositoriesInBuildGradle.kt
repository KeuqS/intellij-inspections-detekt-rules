package by.overpas.detekt.intellij.gradle.bestpractices.avoidrepositoriesinbuildgradle

import by.overpas.detekt.intellij.IntellijInspection
import by.overpas.detekt.intellij.gradle.bestpractices.lambdaBody
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.parents

@IntellijInspection("AvoidRepositoriesInBuildGradle")
class AvoidRepositoriesInBuildGradle(config: Config) :
    Rule(
        config,
        "A build script declares repositories. Declare them one time in `dependencyResolutionManagement` or " +
            "`pluginManagement` of the settings file.",
    ) {

    override fun visitKtFile(file: KtFile) {
        if (file.name == "build.gradle.kts") super.visitKtFile(file)
    }

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        if (expression.calleeExpression?.text != "repositories" || expression.lambdaBody() == null) return
        val isPublishingRepositories = expression.parents
            .any { it is KtCallExpression && it.calleeExpression?.text == "publishing" }
        if (!isPublishingRepositories) {
            report(
                Finding(
                    Entity.from(expression.calleeExpression ?: expression),
                    "It's recommended to declare repositories in the Gradle settings file",
                ),
            )
        }
    }
}

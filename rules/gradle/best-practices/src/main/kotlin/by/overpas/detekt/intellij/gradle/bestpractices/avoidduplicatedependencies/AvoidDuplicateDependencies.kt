package by.overpas.detekt.intellij.gradle.bestpractices.avoidduplicatedependencies

import by.overpas.detekt.intellij.IntellijInspection
import by.overpas.detekt.intellij.gradle.bestpractices.dependencyCoordinates
import by.overpas.detekt.intellij.gradle.bestpractices.isGradleScript
import by.overpas.detekt.intellij.gradle.bestpractices.topLevelBlocks
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtScript

@IntellijInspection("AvoidDuplicateDependencies")
class AvoidDuplicateDependencies(config: Config) :
    Rule(
        config,
        "The `dependencies` blocks of the script declare the same dependency in one configuration more than one " +
            "time. Remove the duplicates.",
    ) {

    override fun visitKtFile(file: KtFile) {
        if (file.isGradleScript()) super.visitKtFile(file)
    }

    override fun visitScript(script: KtScript) {
        super.visitScript(script)
        script.topLevelBlocks("dependencies")
            .asSequence()
            .flatMap { it.statements }
            .filterIsInstance<KtCallExpression>()
            .mapNotNull { dependency -> dependency.dependencyKey()?.let { key -> key to dependency } }
            .groupBy({ it.first }, { it.second })
            .filterValues { it.size > 1 }
            .forEach { (key, duplicates) ->
                duplicates.forEach { dependency ->
                    report(Finding(Entity.from(dependency), "Dependency '${key.second}' is declared multiple times"))
                }
            }
    }

    private fun KtCallExpression.dependencyKey(): Pair<String, String>? {
        val configuration = calleeExpression?.text?.trim('"')
        val coordinates = dependencyCoordinates() ?: valueArgumentList?.arguments?.singleOrNull()?.text
        return if (configuration == null || coordinates == null) null else configuration to coordinates
    }
}

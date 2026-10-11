package by.overpas.detekt.intellij.gradle.bestpractices.redundantkotlinstdlibdependency

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

@IntellijInspection("RedundantKotlinStdLibDependency")
class RedundantKotlinStdLibDependency(config: Config) :
    Rule(
        config,
        "The script declares the `kotlin-stdlib` dependency with the version of the Kotlin JVM plugin. " +
            "The plugin adds this dependency itself. Remove the declaration.",
    ) {

    override fun visitKtFile(file: KtFile) {
        if (file.isGradleScript()) super.visitKtFile(file)
    }

    override fun visitScript(script: KtScript) {
        super.visitScript(script)
        val pluginVersion = script.topLevelBlocks("plugins").firstNotNullOfOrNull { it.kotlinJvmPluginVersion() }
            ?: return
        script.topLevelBlocks("dependencies")
            .asSequence()
            .flatMap { it.statements }
            .filterIsInstance<KtCallExpression>()
            .filter { it.isStdLibOf(pluginVersion) }
            .forEach { dependency ->
                report(
                    Finding(
                        Entity.from(dependency),
                        "Kotlin standard library dependency is already added by the Kotlin plugin",
                    ),
                )
            }
    }

    private fun KtCallExpression.isStdLibOf(version: String): Boolean {
        val configuration = calleeExpression?.text?.trim('"') ?: return false
        val isCompileOnly = configuration.endsWith("compileOnly", ignoreCase = true)
        val argumentCount = valueArgumentList?.arguments.orEmpty().size
        return !isCompileOnly && lambdaArguments.isEmpty() && argumentCount <= MAX_ARGUMENTS &&
            dependencyCoordinates() == "org.jetbrains.kotlin:kotlin-stdlib:$version"
    }

    private companion object {
        const val MAX_ARGUMENTS = 3
    }
}

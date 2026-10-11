package by.overpas.detekt.intellij.jvm.junit.parameterizedparametersstaticcollection

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtClass

@IntellijInspection("ParameterizedParametersStaticCollection")
class ParameterizedParametersStaticCollection(config: Config) :
    Rule(
        config,
        "A JUnit 4 `Parameterized` test class has no data provider method, more than one, or one with a bad " +
            "signature. The data provider must be one public `@JvmStatic` function in a companion object, " +
            "annotated with `@Parameters`, that returns an `Iterable` or an array.",
    ),
    RequiresAnalysisApi {

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)
        if (klass.annotationEntries.isEmpty()) return
        val name = klass.name.orEmpty()
        val providers = analyze(klass) { klass.parameterizedDataProviders() } ?: return
        when {
            providers.isEmpty() -> report(
                Finding(
                    Entity.atName(klass),
                    "Parameterized test class '$name' lacks data provider method annotated with '@Parameters'",
                ),
            )

            providers.size > 1 -> report(
                Finding(Entity.atName(klass), "Multiple @Parameters data provider methods present in class '$name'"),
            )
        }
        providers.filterNot { it.hasValidSignature }.map { it.function }.forEach { provider ->
            report(
                Finding(
                    Entity.atName(provider),
                    "Data provider method '${provider.name.orEmpty()}()' has an incorrect signature",
                ),
            )
        }
    }
}

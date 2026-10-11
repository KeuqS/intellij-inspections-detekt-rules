package by.overpas.detekt.intellij.jvm.junit.junit3styletestmethodinjunit4class

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtNamedFunction

@IntellijInspection("JUnit3StyleTestMethodInJUnit4Class")
class JUnit3StyleTestMethodInJUnit4Class(config: Config) :
    Rule(
        config,
        "A method named `test...` without `@Test` in a JUnit 4 class. JUnit 4 runs only annotated methods, " +
            "so this test does not run. Add `@Test`.",
    ),
    RequiresAnalysisApi {

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        if (!function.isJUnit3StyleTestCandidate()) return
        if (analyze(function) { function.isJUnit3StyleTestInJUnit4Class() }) {
            report(
                Finding(
                    Entity.atName(function),
                    "Old style JUnit test method '${function.name.orEmpty()}()' in JUnit 4 class",
                ),
            )
        }
    }
}

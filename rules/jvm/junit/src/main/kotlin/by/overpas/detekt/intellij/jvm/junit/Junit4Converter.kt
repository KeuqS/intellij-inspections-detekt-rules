package by.overpas.detekt.intellij.jvm.junit

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtClass

@IntellijInspection("Junit4Converter")
class Junit4Converter(config: Config) :
    Rule(
        config,
        "A JUnit 3 test case extends `TestCase` while JUnit 4 is available. " +
            "Convert it to a JUnit 4 test with annotations.",
    ),
    RequiresAnalysisApi {

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)
        if (klass.superTypeListEntries.isEmpty()) return
        val isConvertible = analyze(klass) {
            ORG_JUNIT_TEST.isOnClasspath() && klass.isJUnit3TestCase()
        }
        if (isConvertible) {
            report(Finding(Entity.atName(klass), "'${klass.name.orEmpty()}' could be converted to JUnit4 test case"))
        }
    }
}

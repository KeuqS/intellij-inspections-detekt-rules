package by.overpas.detekt.intellij.jvm.junit

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtClass

@IntellijInspection("JUnit5Converter")
class JUnit5Converter(config: Config) :
    Rule(
        config,
        "A JUnit 4 test class uses no runner, rules or `@Test` parameters while JUnit 5 is available. " +
            "Convert it to a JUnit 5 test.",
    ),
    RequiresAnalysisApi {

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)
        if (klass.functions().all { it.annotationEntries.isEmpty() }) return
        if (analyze(klass) { klass.canBeConvertedToJUnit5() }) {
            report(Finding(Entity.atName(klass), "${klass.name.orEmpty()} can be JUnit 5 test"))
        }
    }
}

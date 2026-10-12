package by.overpas.detekt.intellij.jvm.junit.junitmixedframework

import by.overpas.detekt.intellij.IntellijInspection
import by.overpas.detekt.intellij.jvm.junit.functions
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtClass

@IntellijInspection("JUnitMixedFramework")
class JUnitMixedFramework(config: Config) :
    Rule(
        config,
        "A test class uses annotations of a JUnit version other than the version of the class. " +
            "The runner of the class ignores these annotations, so the method does not run as expected.",
    ),
    RequiresAnalysisApi {

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)
        if (klass.functions().all { it.annotationEntries.isEmpty() }) return
        val mixed = analyze(klass) { klass.mixedJUnitAnnotations() }
        mixed.forEach { (entry, message) -> report(Finding(Entity.from(entry), message)) }
    }
}

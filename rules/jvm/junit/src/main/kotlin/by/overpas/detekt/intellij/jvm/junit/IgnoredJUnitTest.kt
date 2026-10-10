package by.overpas.detekt.intellij.jvm.junit

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction

@IntellijInspection("IgnoredJUnitTest")
class IgnoredJUnitTest(config: Config) :
    Rule(
        config,
        "A test class or test method is disabled with `@Ignore` or `@Disabled`, so it does not run. " +
            "Fix and enable the test, or give the reason why it is disabled.",
    ),
    RequiresAnalysisApi {

    @Configuration("Report only the disabled tests that do not give a reason.")
    private val onlyReportWithoutReason: Boolean by config(true)

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)
        check(klass, "Test class '${klass.name}'")
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        check(function, "Test method '${function.name}()'")
    }

    private fun check(declaration: KtNamedDeclaration, subject: String) {
        if (declaration.annotationEntries.isEmpty()) return
        val annotations = analyze(declaration) { declaration.findAnnotations(ORG_JUNIT_IGNORE, JUPITER_DISABLED) }
        if (annotations.isEmpty()) return
        if (onlyReportWithoutReason && annotations.any { annotation -> annotation.arguments.any { it.name.asString() == "value" } }) {
            return
        }
        val suffix = if (onlyReportWithoutReason) " without reason" else ""
        report(Finding(Entity.atName(declaration), "$subject is ignored/disabled$suffix"))
    }
}

package by.overpas.detekt.intellij.jvm.testframeworks.testcasewithnotestmethods

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClass

@IntellijInspection("TestCaseWithNoTestMethods")
class TestCaseWithNoTestMethods(config: Config) :
    Rule(
        config,
        "A test class has no test methods, so the test runner does not run tests in it. " +
            "Add tests or remove the class.",
    ),
    RequiresAnalysisApi {

    @Configuration("Do not report a test class when a superclass has test methods.")
    private val isSupersIgnored: Boolean by config(true)

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)
        val isCandidate = !klass.isInterface() &&
            !klass.isEnum() &&
            !klass.isAnnotation() &&
            !klass.isLocal &&
            SKIPPED_MODIFIERS.none { klass.hasModifier(it) }
        if (isCandidate && analyze(klass) { klass.hasNoTests(isSupersIgnored) }) {
            report(Finding(Entity.atName(klass), "Test class '${klass.nameAsSafeName}' has no tests"))
        }
    }

    private companion object {
        val SKIPPED_MODIFIERS = listOf(KtTokens.ABSTRACT_KEYWORD, KtTokens.OPEN_KEYWORD, KtTokens.SEALED_KEYWORD)
    }
}

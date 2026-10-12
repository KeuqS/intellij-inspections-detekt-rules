package by.overpas.detekt.intellij.jvm.testframeworks.testcasewithconstructor

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtClass

@IntellijInspection("TestCaseWithConstructor")
class TestCaseWithConstructor(config: Config) :
    Rule(
        config,
        "A test class does initialization work in a constructor or an `init` block. " +
            "Move the initialization into a `setUp()` or `@Before` life cycle method.",
    ),
    RequiresAnalysisApi {

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)
        val hasCandidates = klass.secondaryConstructors.isNotEmpty() || klass.getAnonymousInitializers().isNotEmpty()
        if (klass.isInterface() || !hasCandidates) return
        val findings = analyze(klass) {
            val initializers = klass.nonTrivialInitializers().map { Finding(Entity.from(it), INITIALIZER_MESSAGE) }
            val constructors = klass.nonTrivialConstructors().map { constructor ->
                Finding(Entity.from(constructor.getConstructorKeyword()), CONSTRUCTOR_MESSAGE)
            }
            initializers + constructors
        }
        findings.forEach(::report)
    }

    private companion object {
        const val CONSTRUCTOR_MESSAGE =
            "Initialization logic in constructor 'constructor()' instead of 'setUp()' life cycle method"
        const val INITIALIZER_MESSAGE = "Initialization logic in initializer instead of 'setUp()' life cycle method"
    }
}

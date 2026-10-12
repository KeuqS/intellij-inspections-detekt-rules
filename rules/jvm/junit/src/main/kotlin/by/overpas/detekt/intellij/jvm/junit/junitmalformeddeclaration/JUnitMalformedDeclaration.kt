package by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration

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
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject

@IntellijInspection("JUnitMalformedDeclaration")
class JUnitMalformedDeclaration(config: Config) :
    Rule(
        config,
        "A JUnit test class, test method, lifecycle method, rule, extension or parameter source has a declaration " +
            "that JUnit cannot run or rejects. Change the declaration as the message tells.",
    ),
    RequiresAnalysisApi {

    @Configuration("Fully qualified names of annotations that let a test or lifecycle method declare a parameter.")
    private val ignorableAnnotations: List<String> by config(
        listOf("mockit.Mocked", "org.junit.jupiter.api.io.TempDir"),
    )

    @Configuration("Allow suspend test and lifecycle functions. JUnit 6 and later runs them.")
    private val areSuspendFunctionsAllowed: Boolean by config(false)

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        if (function.containingClassOrObject == null) return
        if (function.annotationEntries.isEmpty() && !function.isJUnit3Candidate()) return
        val settings = settings()
        analyze(function) { context(settings) { function.functionMalformations() } }.forEach(::reportMalformation)
    }

    override fun visitClass(klass: KtClass) {
        super.visitClass(klass)
        analyze(klass) { klass.classMalformations() }.forEach(::reportMalformation)
    }

    override fun visitObjectDeclaration(declaration: KtObjectDeclaration) {
        super.visitObjectDeclaration(declaration)
        if (declaration.isObjectLiteral()) return
        analyze(declaration) { declaration.objectMalformation() }?.let(::reportMalformation)
    }

    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)
        if (property.containingClassOrObject == null || property.annotationEntries.isEmpty()) return
        analyze(property) { property.propertyMalformations() }.forEach(::reportMalformation)
    }

    private fun settings(): MalformedSettings =
        MalformedSettings(ignorableAnnotations.toSet(), areSuspendFunctionsAllowed)

    private fun reportMalformation(malformation: Malformation) =
        report(Finding(Entity.from(malformation.anchor), malformation.message))
}

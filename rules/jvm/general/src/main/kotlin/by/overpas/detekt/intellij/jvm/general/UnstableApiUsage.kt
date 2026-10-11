package by.overpas.detekt.intellij.jvm.general

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtReferenceExpression

@IntellijInspection("UnstableApiUsage")
class UnstableApiUsage(config: Config) :
    Rule(
        config,
        "Code uses a library API that an annotation such as `@ApiStatus.Experimental` or `@ApiStatus.Internal` " +
            "marks as unstable. Such an API can change or go away in a future version.",
    ),
    RequiresAnalysisApi {

    @Configuration("The fully qualified names of the annotations that mark an API as unstable.")
    private val unstableApiAnnotations: List<String> by config(UNSTABLE_API_ANNOTATIONS)

    @Configuration("Do not report the usages inside import directives.")
    private val isInsideImportsIgnored: Boolean by config(true)

    @Configuration("Report only the APIs of compiled libraries and not the APIs declared in the analyzed sources.")
    private val isApiDeclaredInThisProjectIgnored: Boolean by config(true)

    private val checker by lazy {
        val matcher = ApiStatusAnnotationMatcher(unstableApiAnnotations.toSet())
        UnstableApiUsageChecker(
            matcher = matcher,
            signatureMatcher = ApiStatusSignatureMatcher(matcher),
            isInsideImportsIgnored = isInsideImportsIgnored,
            isApiDeclaredInThisProjectIgnored = isApiDeclaredInThisProjectIgnored,
        )
    }

    override fun visitReferenceExpression(expression: KtReferenceExpression) {
        super.visitReferenceExpression(expression)
        if (expression !is KtNameReferenceExpression) return
        analyze(expression) { expression.apiStatusUsages().mapNotNull { checker.message(it) } }
            .forEach { report(Finding(Entity.from(expression), it)) }
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        analyze(function) { function.apiStatusOverrides().mapNotNull { checker.message(it) } }
            .forEach { report(Finding(Entity.from(function.apiStatusAnchor()), it)) }
    }

    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)
        analyze(property) { property.apiStatusOverrides().mapNotNull { checker.message(it) } }
            .forEach { report(Finding(Entity.from(property.apiStatusAnchor()), it)) }
    }
}

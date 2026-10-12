package by.overpas.detekt.intellij.jvm.codematurity.markedforremoval

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
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtSimpleNameExpression

@IntellijInspection("MarkedForRemoval")
class MarkedForRemoval(config: Config) :
    Rule(
        config,
        "An API that is deprecated with `@Deprecated(forRemoval = true)` is used. A future version can remove " +
            "the API, and then the code does not compile. Stop using the API.",
    ),
    RequiresAnalysisApi {

    @Configuration("Do not report the usages in the same outermost class as the API.")
    private val isSameOutermostClassIgnored: Boolean by config(true)

    override fun visitSimpleNameExpression(expression: KtSimpleNameExpression) {
        super.visitSimpleNameExpression(expression)
        if (expression !is KtNameReferenceExpression) return
        val usage = analyze(expression) { expression.removalUsage() } ?: return
        if (!(isSameOutermostClassIgnored && usage.isInSameOutermostClass(expression))) {
            report(Finding(Entity.from(expression), usage.message))
        }
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        if (!function.hasModifier(KtTokens.OVERRIDE_KEYWORD)) return
        analyze(function) { function.overriddenRemovalClasses() }.forEach { owner ->
            report(
                Finding(
                    Entity.atName(function),
                    "Overrides method that is deprecated and marked for removal in '$owner'",
                ),
            )
        }
    }
}

package by.overpas.detekt.intellij.android.kotlin.k2typeparameterfindviewbyid

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtCallExpression

@IntellijInspection("K2TypeParameterFindViewById")
class K2TypeParameterFindViewById(config: Config) :
    Rule(
        config,
        "A `findViewById`, `findViewWithTag` or `requireViewById` call result is cast to a view type. The function " +
            "has a type parameter that makes the cast unnecessary. Give the view type as a type argument.",
    ),
    RequiresAnalysisApi {

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        val (cast, suggestion) = expression.findViewCast() ?: return
        if (analyze(expression) { expression.canTakeTypeArgumentFrom(cast) }) {
            report(Finding(Entity.from(cast), "Can be converted to $suggestion"))
        }
    }
}

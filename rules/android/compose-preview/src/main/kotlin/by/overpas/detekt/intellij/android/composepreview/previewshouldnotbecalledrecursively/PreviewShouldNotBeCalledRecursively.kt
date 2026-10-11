package by.overpas.detekt.intellij.android.composepreview.previewshouldnotbecalledrecursively

import by.overpas.detekt.intellij.IntellijInspection
import by.overpas.detekt.intellij.android.composepreview.previewAnnotationEntries
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

@IntellijInspection("PreviewShouldNotBeCalledRecursively")
class PreviewShouldNotBeCalledRecursively(config: Config) :
    Rule(
        config,
        "A function with `@Preview` or a MultiPreview annotation calls a function with the same name. A preview " +
            "function usually does not call itself, and the call can be a mistake. Make sure that the call is correct.",
    ),
    RequiresAnalysisApi {

    override fun visitCallExpression(expression: KtCallExpression) {
        super.visitCallExpression(expression)
        val function = expression.getStrictParentOfType<KtNamedFunction>() ?: return
        val name = function.name ?: return
        val isRecursive = analyze(expression) {
            expression.calleeName()?.asString() == name && function.previewAnnotationEntries().isNotEmpty()
        }
        if (isRecursive) {
            report(
                Finding(
                    Entity.from(expression),
                    "Preview functions usually don't call themselves recursively, so please double-check you're " +
                        "calling the intended function",
                ),
            )
        }
    }
}

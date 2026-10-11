package by.overpas.detekt.intellij.kotlin.styleissues.replacesizecheckwithisnotempty

import by.overpas.detekt.intellij.IntellijInspection
import by.overpas.detekt.intellij.kotlin.styleissues.isInsideSizeCheckReplacement
import by.overpas.detekt.intellij.kotlin.styleissues.isReplaceableSizeCheckCall
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtBinaryExpression

@IntellijInspection("ReplaceSizeCheckWithIsNotEmpty")
class ReplaceSizeCheckWithIsNotEmpty(config: Config) :
    Rule(
        config,
        "A comparison of a size or a length with zero is verbose. Call `isNotEmpty()` instead.",
    ),
    RequiresAnalysisApi {

    override fun visitBinaryExpression(expression: KtBinaryExpression) {
        super.visitBinaryExpression(expression)
        val sizeCall = expression.sizeCheckWithIsNotEmptyTarget() ?: return
        if (!expression.isInsideSizeCheckReplacement("isNotEmpty") && sizeCall.isReplaceableSizeCheckCall()) {
            report(Finding(Entity.from(expression), "Size check can be replaced with 'isNotEmpty()'"))
        }
    }
}

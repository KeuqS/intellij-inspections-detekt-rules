package by.overpas.detekt.intellij.jvm.general

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtReferenceExpression

@IntellijInspection("OverrideOnly")
class OverrideOnly(config: Config) :
    Rule(
        config,
        "Code calls or references a library method that `@ApiStatus.OverrideOnly` marks as intended only for " +
            "overriding, or the annotation is on a declaration that cannot be overridden.",
    ),
    RequiresAnalysisApi {

    override fun visit(root: KtFile) {
        if (analyze(root) { findClass(API_STATUS_OVERRIDE_ONLY) != null }) super.visit(root)
    }

    override fun visitDeclaration(dcl: KtDeclaration) {
        super.visitDeclaration(dcl)
        if (dcl.annotationEntries.isEmpty()) return
        val message = analyze(dcl) { targetChecker.message(dcl) }
        if (message != null) report(Finding(Entity.from(dcl.apiStatusAnchor()), message))
    }

    override fun visitReferenceExpression(expression: KtReferenceExpression) {
        super.visitReferenceExpression(expression)
        if (expression !is KtNameReferenceExpression) return
        analyze(expression) { expression.apiStatusUsages().mapNotNull { it.overrideOnlyMethodName() } }
            .forEach { report(Finding(Entity.from(expression), "Method '$it' can only be overridden")) }
    }

    private companion object {
        val targetChecker = ApiStatusTargetChecker(
            annotation = API_STATUS_OVERRIDE_ONLY,
            annotationName = "ApiStatus.OverrideOnly",
            classConsequence = "it cannot be extended, nor its methods overridden",
        )
    }
}

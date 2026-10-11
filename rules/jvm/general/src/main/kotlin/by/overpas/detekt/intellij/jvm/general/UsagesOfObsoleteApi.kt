package by.overpas.detekt.intellij.jvm.general

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtReferenceExpression

@IntellijInspection("UsagesOfObsoleteApi")
class UsagesOfObsoleteApi(config: Config) :
    Rule(
        config,
        "Code uses an API that `@ApiStatus.Obsolete` marks as obsolete. " +
            "Use the newer API that replaces it, if there is one.",
    ),
    RequiresAnalysisApi {

    override fun visit(root: KtFile) {
        if (analyze(root) { findClass(API_STATUS_OBSOLETE) != null }) super.visit(root)
    }

    override fun visitReferenceExpression(expression: KtReferenceExpression) {
        super.visitReferenceExpression(expression)
        if (expression !is KtNameReferenceExpression) return
        analyze(expression) { expression.apiStatusUsages().obsoleteElements() }
            .forEach { report(Finding(Entity.from(it), MESSAGE)) }
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        analyze(function) { function.apiStatusOverrides().obsoleteElements() }
            .forEach { report(Finding(Entity.from(it), MESSAGE)) }
    }

    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)
        analyze(property) { property.apiStatusOverrides().obsoleteElements() }
            .forEach { report(Finding(Entity.from(it), MESSAGE)) }
    }

    private companion object {
        const val MESSAGE = "Obsolete API is used"
    }
}

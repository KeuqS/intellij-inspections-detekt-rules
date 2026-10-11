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
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.KtSuperTypeListEntry
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

@IntellijInspection("NonExtendableApiUsage")
class NonExtendableApiUsage(config: Config) :
    Rule(
        config,
        "Code extends, implements or overrides a library API that `@ApiStatus.NonExtendable` marks as not " +
            "intended for extension, or the annotation is on a declaration that cannot be extended.",
    ),
    RequiresAnalysisApi {

    override fun visit(root: KtFile) {
        if (analyze(root) { findClass(API_STATUS_NON_EXTENDABLE) != null }) super.visit(root)
    }

    override fun visitDeclaration(dcl: KtDeclaration) {
        super.visitDeclaration(dcl)
        if (dcl.annotationEntries.isEmpty()) return
        val message = analyze(dcl) { targetChecker.message(dcl) }
        if (message != null) report(Finding(Entity.from(dcl.apiStatusAnchor()), message))
    }

    override fun visitReferenceExpression(expression: KtReferenceExpression) {
        super.visitReferenceExpression(expression)
        if (expression !is KtNameReferenceExpression || expression.getParentOfType<KtSuperTypeListEntry>(
                true,
            ) == null
        ) {
            return
        }
        analyze(expression) { expression.apiStatusUsages().mapNotNull { it.nonExtendableSuperTypeMessage() } }
            .forEach { report(Finding(Entity.from(expression), it)) }
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        analyze(function) { function.apiStatusOverrides().mapNotNull { it.nonExtendableOverrideMessage() } }
            .forEach { report(Finding(Entity.from(function.apiStatusAnchor()), it)) }
    }

    override fun visitProperty(property: KtProperty) {
        super.visitProperty(property)
        analyze(property) { property.apiStatusOverrides().mapNotNull { it.nonExtendableOverrideMessage() } }
            .forEach { report(Finding(Entity.from(property.apiStatusAnchor()), it)) }
    }

    private companion object {
        val targetChecker = ApiStatusTargetChecker(
            annotation = API_STATUS_NON_EXTENDABLE,
            annotationName = "ApiStatus.NonExtendable",
            classConsequence = "it cannot be extended",
        )
    }
}

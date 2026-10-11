package by.overpas.detekt.intellij.jvm.general

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbolModality
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbolVisibility
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject

internal class ApiStatusTargetChecker(
    private val annotation: ClassId,
    private val annotationName: String,
    private val classConsequence: String,
) {

    context(session: KaSession)
    fun message(declaration: KtDeclaration): String? {
        val isAnnotated = declaration.isApiStatusDeclaration() && declaration.findApiStatusEntry(annotation) != null
        return when {
            !isAnnotated -> null

            declaration is KtClassOrObject ->
                "Class '${declaration.name.orEmpty()}' is marked with '@$annotationName', but $classConsequence"
                    .takeIf { declaration.isFinalApiStatusClass() }

            declaration is KtNamedFunction -> declaration.methodMessage()

            else -> null
        }
    }

    context(session: KaSession)
    private fun KtNamedFunction.methodMessage(): String? {
        val owner = containingClassOrObject
        val method = with(session) { symbol }
        val isOpenOwner = owner != null && !owner.isFinalApiStatusClass()
        val isOverridable = isOpenOwner &&
            method.modality != KaSymbolModality.FINAL &&
            method.visibility != KaSymbolVisibility.PRIVATE
        return when {
            isOpenOwner && owner.findApiStatusEntry(annotation) != null -> "Annotation '@$annotationName' is redundant"

            !isOverridable ->
                "Method '${method.apiStatusName(isQualified = false)}' is marked with '@$annotationName', " +
                    "but it cannot be overridden"

            else -> null
        }
    }
}

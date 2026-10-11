package by.overpas.detekt.intellij.android.glancepreview.glancepreviewmustbetoplevelfunction

import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

internal fun KtNamedFunction.isValidPreviewLocation(): Boolean =
    isTopLevel || (getStrictParentOfType<KtNamedFunction>() == null && isInInstantiableClass())

private fun KtNamedFunction.isInInstantiableClass(): Boolean {
    val containingClass = getStrictParentOfType<KtClass>() ?: return false
    val constructors = listOfNotNull(containingClass.primaryConstructor) + containingClass.secondaryConstructors
    return containingClass.isTopLevel() &&
        (constructors.isEmpty() || constructors.any { it.valueParameters.isEmpty() })
}

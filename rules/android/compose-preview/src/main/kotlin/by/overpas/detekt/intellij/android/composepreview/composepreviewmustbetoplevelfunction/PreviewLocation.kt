package by.overpas.detekt.intellij.android.composepreview.composepreviewmustbetoplevelfunction

import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType

internal fun KtNamedFunction.isValidPreviewLocation(): Boolean =
    isTopLevel ||
        (getStrictParentOfType<KtNamedFunction>() == null && containingClass()?.isInstantiableTopLevel() == true)

private fun KtClass.isInstantiableTopLevel(): Boolean {
    val constructors = listOfNotNull(primaryConstructor) + secondaryConstructors
    return isTopLevel() && (constructors.isEmpty() || constructors.any { it.valueParameters.isEmpty() })
}

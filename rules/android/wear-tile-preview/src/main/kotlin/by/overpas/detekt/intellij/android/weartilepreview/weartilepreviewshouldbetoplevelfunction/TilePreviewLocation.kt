package by.overpas.detekt.intellij.android.weartilepreview.weartilepreviewshouldbetoplevelfunction

import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject

internal fun KtNamedFunction.isValidTilePreviewLocation(): Boolean {
    val containingClass = containingClassOrObject
    return when {
        isLocal -> false
        containingClass == null -> true
        else -> containingClass.isTopLevel() && containingClass.hasDefaultConstructor()
    }
}

private fun KtClassOrObject.hasDefaultConstructor(): Boolean {
    if (this !is KtClass) return true
    val primary = primaryConstructor
    return (primary == null && secondaryConstructors.isEmpty()) ||
        primary?.run { valueParameters.all { it.hasDefaultValue() } } == true ||
        secondaryConstructors.any { it.valueParameters.isEmpty() }
}

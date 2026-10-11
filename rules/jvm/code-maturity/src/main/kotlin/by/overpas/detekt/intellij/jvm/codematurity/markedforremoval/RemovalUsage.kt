package by.overpas.detekt.intellij.jvm.codematurity.markedforremoval

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.parentsWithSelf

internal class RemovalUsage(
    private val name: String,
    private val since: String?,
    private val declaration: PsiElement?,
) {

    val message: String
        get() = if (since.isNullOrBlank()) {
            "'$name' is deprecated and marked for removal"
        } else {
            "'$name' is deprecated since version $since and marked for removal"
        }

    fun isInSameOutermostClass(usage: PsiElement): Boolean =
        declaration != null &&
            declaration.containingFile == usage.containingFile &&
            declaration.outermostContainer() == usage.outermostContainer()

    private fun PsiElement.outermostContainer(): PsiElement =
        parentsWithSelf.lastOrNull { it is KtClassOrObject } ?: containingFile
}

package by.overpas.detekt.intellij.jvm.general

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbol
import org.jetbrains.kotlin.psi.KtExpression

internal data class ApiStatusUsage(
    val element: PsiElement,
    val target: KaSymbol,
    val kind: Kind,
    val qualifier: KtExpression? = null,
) {

    enum class Kind { REFERENCE, IMPORT, CONSTRUCTOR, OVERRIDE }
}

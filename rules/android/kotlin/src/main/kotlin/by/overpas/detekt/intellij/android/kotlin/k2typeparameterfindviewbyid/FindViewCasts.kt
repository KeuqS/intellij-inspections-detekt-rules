package by.overpas.detekt.intellij.android.kotlin.k2typeparameterfindviewbyid

import org.jetbrains.kotlin.psi.KtBinaryExpressionWithTypeRHS
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNullableType
import org.jetbrains.kotlin.psi.KtPsiUtil
import org.jetbrains.kotlin.psi.psiUtil.getBinaryWithTypeParent

internal val FIND_VIEW_FUNCTION_NAMES = setOf("findViewById", "findViewWithTag", "requireViewById")

internal data class FindViewCast(
    val expression: KtBinaryExpressionWithTypeRHS,
    val suggestion: String,
)

internal fun KtCallExpression.findViewCast(): FindViewCast? {
    val callee = calleeExpression?.takeIf { it.text in FIND_VIEW_FUNCTION_NAMES && typeArguments.isEmpty() }
    val cast = callee?.getBinaryWithTypeParent()?.takeIf(KtPsiUtil::isUnsafeCast)
    val typeText = cast?.right?.typeElement?.let { type -> (type as? KtNullableType)?.innerType?.text ?: type.text }
    return if (callee != null && cast != null && typeText != null) {
        FindViewCast(cast, "${callee.text}<$typeText>(...)")
    } else {
        null
    }
}

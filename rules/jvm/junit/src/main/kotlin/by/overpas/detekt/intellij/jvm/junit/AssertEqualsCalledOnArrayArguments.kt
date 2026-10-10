package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression

context(session: KaSession)
internal fun KtCallExpression.isAssertEqualsOnArrays(): Boolean {
    val call = resolvedFunctionCall() ?: return false
    val owner = call.ownerClassId?.asFqNameString().orEmpty()
    val hint = call.assertEqualsHint()?.takeIf { "junit" in owner } ?: return false
    return hint.firstArgument.isArray() && hint.secondArgument.isArray()
}

context(session: KaSession)
private fun KtExpression?.isArray(): Boolean =
    with(session) { this@isArray?.expressionType?.isArrayOrPrimitiveArray == true }

package by.overpas.detekt.intellij.jvm.junit.assertequalscalledonarray

import by.overpas.detekt.intellij.jvm.junit.assertEqualsHint
import by.overpas.detekt.intellij.jvm.junit.ownerClassId
import by.overpas.detekt.intellij.jvm.junit.resolvedFunctionCall
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression

context(session: KaSession)
internal fun KtCallExpression.isAssertEqualsOnArrays(): Boolean {
    val call = resolvedFunctionCall()
    val isJUnit = "junit" in call?.ownerClassId?.asFqNameString().orEmpty()
    val (first, second) = call?.assertEqualsHint()?.takeIf { isJUnit } ?: return false
    return first.isArray() && second.isArray()
}

context(session: KaSession)
private fun KtExpression?.isArray(): Boolean =
    with(session) { this@isArray?.expressionType?.isArrayOrPrimitiveArray == true }

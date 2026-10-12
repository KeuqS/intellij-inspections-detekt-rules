package by.overpas.detekt.intellij.jvm.junit.migrateasserttomatcherassert

import by.overpas.detekt.intellij.jvm.junit.HAMCREST_CORE_MATCHERS
import by.overpas.detekt.intellij.jvm.junit.HAMCREST_MATCHERS
import by.overpas.detekt.intellij.jvm.junit.HAMCREST_ORDERING_COMPARISON
import by.overpas.detekt.intellij.jvm.junit.JUNIT_FRAMEWORK_ASSERT
import by.overpas.detekt.intellij.jvm.junit.ORG_JUNIT_ASSERT
import by.overpas.detekt.intellij.jvm.junit.isOnClasspath
import by.overpas.detekt.intellij.jvm.junit.ownerClassId
import by.overpas.detekt.intellij.jvm.junit.resolvedFunctionCall
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtPsiUtil

internal val MATCHER_ASSERT_METHODS = setOf(
    "assertArrayEquals",
    "assertEquals",
    "assertNotEquals",
    "assertSame",
    "assertNotSame",
    "assertNotNull",
    "assertNull",
    "assertTrue",
    "assertFalse",
)

private val BOOLEAN_ASSERT_METHODS = setOf("assertTrue", "assertFalse")

context(session: KaSession)
internal fun KtCallExpression.isMatcherAssertCandidate(): Boolean {
    val owner = resolvedFunctionCall()?.ownerClassId
    val hasMatchers = HAMCREST_MATCHERS.isOnClasspath() || HAMCREST_CORE_MATCHERS.isOnClasspath()
    val isOrderingNeeded = calleeExpression?.text in BOOLEAN_ASSERT_METHODS && lastArgumentIsBinaryExpression()
    return hasMatchers &&
        owner in setOf(ORG_JUNIT_ASSERT, JUNIT_FRAMEWORK_ASSERT) &&
        (!isOrderingNeeded || HAMCREST_ORDERING_COMPARISON.isOnClasspath())
}

private fun KtCallExpression.lastArgumentIsBinaryExpression(): Boolean {
    val argument = KtPsiUtil.safeDeparenthesize(valueArguments.lastOrNull()?.getArgumentExpression() ?: return false)
    return argument is KtBinaryExpression
}

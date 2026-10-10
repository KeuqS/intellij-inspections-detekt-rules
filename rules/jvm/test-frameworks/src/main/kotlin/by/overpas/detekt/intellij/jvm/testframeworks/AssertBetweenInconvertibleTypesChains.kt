package by.overpas.detekt.intellij.jvm.testframeworks

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression

context(session: KaSession)
internal fun KtExpression.startsWithAssertThat(): Boolean =
    generateSequence(this) { (it as? KtQualifiedExpression)?.receiverExpression }
        .map { (it as? KtQualifiedExpression)?.selectorExpression ?: it }
        .any { (it as? KtCallExpression)?.isAssertThat() == true }

context(session: KaSession)
private fun KtCallExpression.isAssertThat(): Boolean =
    calleeExpression?.text == "assertThat" &&
        valueArguments.size == 1 &&
        resolveCall()?.run { symbol.callableId?.classId } == ASSERTJ_ASSERTIONS

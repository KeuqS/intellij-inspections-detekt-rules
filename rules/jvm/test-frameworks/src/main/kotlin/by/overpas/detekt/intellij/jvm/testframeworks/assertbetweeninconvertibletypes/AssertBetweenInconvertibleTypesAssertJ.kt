package by.overpas.detekt.intellij.jvm.testframeworks.assertbetweeninconvertibletypes

import by.overpas.detekt.intellij.jvm.testframeworks.ASSERTJ_ASSERT
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.types.KaClassType
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.kotlin.psi.psiUtil.getQualifiedExpressionForSelector

private const val ASSERTJ_ACTUAL_TYPE_INDEX = 1

context(session: KaSession)
internal fun KtCallExpression.assertJInconvertibleTypes(): Pair<String, String>? {
    val receiver = getQualifiedExpressionForSelector()?.receiverExpression
    val call = if (receiver?.startsWithAssertThat() == true) resolveCall() else null
    val parameter = call?.run { symbol.valueParameters.singleOrNull() }
    val isEqualityCall = parameter?.run { returnType.isObjectType() } == true
    val actualType = receiver?.let { start ->
        generateSequence(start) { (it as? KtQualifiedExpression)?.receiverExpression }
            .firstNotNullOfOrNull { it.assertJActualType() }
    }
    val expectedArgument = valueArguments.singleOrNull()?.getArgumentExpression()
    val expectedType = expectedArgument?.let { argument -> with(session) { argument.expressionType } }
    return if (isEqualityCall && actualType != null && expectedType != null) {
        inconvertibleTypes(actualType, expectedType)
    } else {
        null
    }
}

context(session: KaSession)
private fun KtExpression.assertJActualType(): KaType? {
    val type = with(session) { expressionType?.lowerBoundIfFlexible() }
    val assertType = type?.let { start ->
        with(session) { sequenceOf(start) + start.allSupertypes }
            .filterIsInstance<KaClassType>()
            .firstOrNull { it.classId == ASSERTJ_ASSERT }
    }
    return assertType?.run { typeArguments.getOrNull(ASSERTJ_ACTUAL_TYPE_INDEX)?.type }
}

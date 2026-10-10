package by.overpas.detekt.intellij.jvm.testframeworks

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.psi.KtCallExpression

context(session: KaSession)
internal fun KtCallExpression.assertInconvertibleTypes(): Pair<String, String>? {
    val call = resolveCall()
    val arguments = call?.run { valueArgumentMapping.entries }.orEmpty()
        .asSequence()
        .filter { it.value.returnType.isObjectType() }
        .map { it.key }
        .sortedBy { it.textOffset }
        .toList()
    val types = arguments.mapNotNull { with(session) { it.expressionType } }
    val isAssertion = call?.run { symbol.callableId?.classId } in ASSERT_OWNERS
    return if (isAssertion && arguments.size == 2 && types.size == 2) inconvertibleTypes(types[0], types[1]) else null
}

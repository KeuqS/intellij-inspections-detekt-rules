package by.overpas.detekt.intellij.jvm.general

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaFunctionSymbol
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtCallableReferenceExpression
import org.jetbrains.kotlin.psi.KtConstantExpression

private val URL_METHOD_OWNER = ClassId.fromString("java/net/URL")

internal fun KaFunctionSymbol.urlMethodName(): String? {
    val callableId = callableId ?: return null
    val name = callableId.callableName.asString()
    val isUrlMethod = callableId.classId == URL_METHOD_OWNER &&
        ((name == "hashCode" && valueParameters.isEmpty()) || (name == "equals" && valueParameters.size == 1))
    return name.takeIf { isUrlMethod }
}

context(session: KaSession)
internal fun KtCallableReferenceExpression.urlMethodReference(): String? =
    with(session) { (callableReference.mainReference.resolveToSymbol() as? KaFunctionSymbol)?.urlMethodName() }

context(session: KaSession)
internal fun KtBinaryExpression.isUrlEquality(): Boolean =
    with(session) {
        val operands = listOfNotNull(left, right)
        operands.size == 2 &&
            operands.none { it is KtConstantExpression && it.text == "null" } &&
            operands.any { UrlHashCodeType(it.expressionType).isUrl() }
    }

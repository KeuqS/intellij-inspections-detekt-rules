package by.overpas.detekt.intellij.jvm.general.urlhashcode

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.resolution.successfulFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtQualifiedExpression

private val MAP_KEY_OPERATIONS = setOf(
    "compute", "computeIfAbsent", "computeIfPresent", "containsKey", "get", "getOrDefault", "merge", "put",
    "putIfAbsent", "remove", "replace",
)

private val SET_OPERATIONS = setOf("add", "contains", "equals", "remove")

internal class UrlHashCodeCall(private val expression: KtCallExpression) {

    private val argument: KtExpression? = expression.valueArguments.firstOrNull()?.getArgumentExpression()

    context(session: KaSession)
    fun urlMethod(): String? =
        with(session) {
            val symbol = expression.resolveToCall()?.successfulFunctionCallOrNull()?.symbol
            symbol?.urlMethodName()?.takeIf { it != "equals" || argument?.text != "null" }
        }

    context(session: KaSession)
    fun isUrlCollectionOperation(): Boolean =
        with(session) {
            val symbol = expression.resolveToCall()?.successfulFunctionCallOrNull()?.symbol
            val owner = symbol?.containingDeclaration as? KaClassSymbol
            val collection = when (symbol?.callableId?.run { callableName.asString() }) {
                in MAP_KEY_OPERATIONS -> findClass(StandardClassIds.Map)
                in SET_OPERATIONS -> findClass(StandardClassIds.Set)
                else -> null
            }
            owner != null &&
                collection != null &&
                (owner == collection || owner.isSubClassOf(collection)) &&
                UrlHashCodeType(argument?.expressionType).isUrl()
        }

    fun receiver(): KtExpression? =
        (expression.parent as? KtQualifiedExpression)?.takeIf {
            it.selectorExpression == expression
        }?.receiverExpression
}

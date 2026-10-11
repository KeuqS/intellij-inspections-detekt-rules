package by.overpas.detekt.intellij.jvm.general

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaClassLikeSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedFunctionSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbolModality
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbolVisibility
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtSuperExpression
import org.jetbrains.kotlin.psi.psiUtil.parents

context(session: KaSession)
internal fun ApiStatusUsage.overrideOnlyMethodName(): String? {
    val method = target as? KaNamedFunctionSymbol ?: return null
    val owner = with(session) { method.containingDeclaration }
    val isOverridable = method.modality != KaSymbolModality.FINAL &&
        method.visibility != KaSymbolVisibility.PRIVATE &&
        !method.isStatic
    val isOverrideOnly = method.hasApiStatusAnnotation(API_STATUS_OVERRIDE_ONLY) ||
        owner?.hasApiStatusAnnotation(API_STATUS_OVERRIDE_ONLY) == true
    val isReported = kind == ApiStatusUsage.Kind.REFERENCE &&
        isOverridable &&
        method.isApiStatusLibrary() &&
        isOverrideOnly &&
        !isSuperOrDelegateCall(method)
    return method.apiStatusName(isQualified = false).takeIf { isReported }
}

context(session: KaSession)
private fun ApiStatusUsage.isSuperOrDelegateCall(method: KaNamedFunctionSymbol): Boolean {
    val owner = with(session) { method.containingDeclaration } as? KaClassLikeSymbol
    val container = element.parents.filterIsInstance<KtNamedFunction>().firstOrNull { !it.isLocal }
    val containerSymbol = container?.let { with(session) { it.symbol } } as? KaNamedFunctionSymbol
    val hasSameSignature = containerSymbol?.hasSignatureOf(method) == true
    val qualifierType = qualifier?.let { with(session) { it.expressionType } }
    val isDelegate = owner != null && qualifierType != null && with(session) { qualifierType.isSubtypeOf(owner) }
    return hasSameSignature && (qualifier is KtSuperExpression || isDelegate)
}

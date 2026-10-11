package by.overpas.detekt.intellij.jvm.general

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.resolution.successfulFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaCallableSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaClassLikeSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaConstructorSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaValueParameterSymbol
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtPackageDirective
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

context(session: KaSession)
internal fun KtNameReferenceExpression.apiStatusUsages(): List<ApiStatusUsage> {
    if (getParentOfType<KtPackageDirective>(true) != null) return emptyList()
    val isImport = getParentOfType<KtImportDirective>(true) != null
    val call = if (isImport) null else apiStatusCallElement()
    val constructor = with(session) { call?.resolveToCall()?.successfulFunctionCallOrNull()?.symbol }
        as? KaConstructorSymbol
    val kind = if (isImport) ApiStatusUsage.Kind.IMPORT else ApiStatusUsage.Kind.REFERENCE
    val qualifier = apiStatusQualifier()
    val references = with(session) { mainReference.resolveToSymbols() }
        .asSequence()
        .map { if (it is KaConstructorSymbol) with(session) { it.containingDeclaration } ?: it else it }
        .filter { (it is KaClassLikeSymbol || it is KaCallableSymbol) && it !is KaValueParameterSymbol }
        .map { ApiStatusUsage(element = this, target = it.apiStatusTarget(), kind = kind, qualifier = qualifier) }
        .toList()
    val constructors = listOfNotNull(constructor?.let { ApiStatusUsage(this, it, ApiStatusUsage.Kind.CONSTRUCTOR) })
    return references + constructors
}

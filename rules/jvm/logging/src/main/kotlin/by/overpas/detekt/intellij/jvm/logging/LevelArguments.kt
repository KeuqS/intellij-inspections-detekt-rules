package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.resolution.successfulFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaEnumEntrySymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaJavaFieldSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaPropertySymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaVariableSymbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression

context(session: KaSession)
internal fun KtCallExpression.levelByFirstArgument(
    levelClasses: Set<ClassId>,
    names: LevelNames,
): String? {
    val argument = valueArguments.firstOrNull()?.getArgumentExpression()
        ?.takeIf { it is KtNameReferenceExpression || it is KtDotQualifiedExpression }
        ?: return null
    val function = with(session) { resolveToCall()?.successfulFunctionCallOrNull() }
    val parameterType = function?.run { symbol.valueParameters.firstOrNull()?.returnType }
    val isLevelParameter = parameterType != null && levelClasses.any { with(session) { parameterType.isSubtypeOf(it) } }
    val field = argument.resolvedSymbol()
        ?.takeIf { isLevelParameter && (it is KaJavaFieldSymbol || it is KaEnumEntrySymbol || it is KaPropertySymbol) }
    return (field as? KaVariableSymbol)?.run { names.byName(name.asString()) }
}

package by.overpas.detekt.intellij.jvm.general

import org.jetbrains.kotlin.analysis.api.KaExperimentalApi
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaConstructorSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaDeclarationSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaFunctionSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaVariableSymbol
import org.jetbrains.kotlin.analysis.api.symbols.typeParameters
import org.jetbrains.kotlin.analysis.api.types.KaClassType
import org.jetbrains.kotlin.analysis.api.types.KaFlexibleType
import org.jetbrains.kotlin.analysis.api.types.KaType

internal class ApiStatusSignatureMatcher(private val matcher: ApiStatusAnnotationMatcher) {

    @OptIn(KaExperimentalApi::class)
    context(session: KaSession)
    fun find(symbol: KaSymbol): ApiStatusAnnotatedDeclaration? {
        val bounds = (symbol as? KaDeclarationSymbol)?.typeParameters.orEmpty().flatMap { it.upperBounds }
        val types = when (symbol) {
            is KaConstructorSymbol -> symbol.valueParameters.map { it.returnType } + bounds

            is KaFunctionSymbol -> buildList {
                addAll(listOfNotNull(symbol.receiverParameter?.returnType))
                symbol.valueParameters.mapTo(this) { it.returnType }
                add(symbol.returnType)
                addAll(bounds)
            }

            is KaVariableSymbol -> listOf(symbol.returnType)

            else -> bounds
        }
        return types.firstNotNullOfOrNull { findInType(it) }
    }

    context(session: KaSession)
    private fun findInType(type: KaType): ApiStatusAnnotatedDeclaration? =
        when (type) {
            is KaFlexibleType -> findInType(type.lowerBound)

            is KaClassType -> with(session) { type.expandedSymbol }?.let { matcher.findAnnotated(it) }
                ?: type.typeArguments.firstNotNullOfOrNull { argument -> argument.type?.let { findInType(it) } }

            else -> null
        }
}

package by.overpas.detekt.intellij.kotlin.styleissues.addvariancemodifier

import org.jetbrains.kotlin.analysis.api.KaExperimentalApi
import org.jetbrains.kotlin.analysis.api.symbols.KaTypeParameterSymbol
import org.jetbrains.kotlin.analysis.api.symbols.typeParameters
import org.jetbrains.kotlin.analysis.api.types.KaClassType
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.analysis.api.types.KaTypeArgumentWithVariance
import org.jetbrains.kotlin.analysis.api.types.KaTypeParameterType
import org.jetbrains.kotlin.types.Variance

internal class AddVarianceModifierPositions(private val typeParameter: KaTypeParameterSymbol) {

    @OptIn(KaExperimentalApi::class)
    fun inType(
        type: KaType,
        position: Variance,
    ): List<Variance> =
        when (type) {
            is KaTypeParameterType -> listOf(position).filter { type.symbol == typeParameter }

            is KaClassType -> type.typeArguments.withIndex().flatMap { (index, argument) ->
                val projection = (argument as? KaTypeArgumentWithVariance)?.variance
                val declared = type.symbol.typeParameters.getOrNull(index)?.variance ?: Variance.INVARIANT
                val variance = projection.takeUnless { projection == Variance.INVARIANT } ?: declared
                argument.type?.let { argumentType -> inType(argumentType, position.superpose(variance)) }.orEmpty()
            }

            else -> emptyList()
        }
}

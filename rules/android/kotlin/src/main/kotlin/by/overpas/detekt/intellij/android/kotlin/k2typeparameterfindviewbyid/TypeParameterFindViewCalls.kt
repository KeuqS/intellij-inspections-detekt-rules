package by.overpas.detekt.intellij.android.kotlin.k2typeparameterfindviewbyid

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.resolution.successfulFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedFunctionSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaTypeParameterSymbol
import org.jetbrains.kotlin.analysis.api.types.KaCapturedType
import org.jetbrains.kotlin.analysis.api.types.KaDefinitelyNotNullType
import org.jetbrains.kotlin.analysis.api.types.KaErrorType
import org.jetbrains.kotlin.analysis.api.types.KaFlexibleType
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.analysis.api.types.KaTypeParameterType
import org.jetbrains.kotlin.psi.KtBinaryExpressionWithTypeRHS
import org.jetbrains.kotlin.psi.KtCallExpression

context(session: KaSession)
internal fun KtCallExpression.canTakeTypeArgumentFrom(cast: KtBinaryExpressionWithTypeRHS): Boolean =
    with(session) {
        val callee = (resolveToCall()?.successfulFunctionCallOrNull()?.symbol as? KaNamedFunctionSymbol)
            ?.takeIf { function -> function.name.asString() in FIND_VIEW_FUNCTION_NAMES }
        val typeParameter = callee?.run {
            typeParameters.singleOrNull()?.takeIf { parameter -> parameter == returnType.typeParameterSymbol() }
        }
        val targetType = cast.right?.run { type.takeUnless { it is KaErrorType } }
            ?.withNullability(isMarkedNullable = false)
        typeParameter != null &&
            targetType != null &&
            typeParameter.upperBounds.all { bound -> targetType.isSubtypeOf(bound) }
    }

context(session: KaSession)
private tailrec fun KaType.typeParameterSymbol(): KaTypeParameterSymbol? =
    when (val expanded = with(session) { fullyExpandedType }) {
        is KaTypeParameterType -> expanded.symbol
        is KaFlexibleType -> expanded.upperBound.typeParameterSymbol()
        is KaDefinitelyNotNullType -> expanded.original.typeParameterSymbol()
        is KaCapturedType -> expanded.projection.type?.typeParameterSymbol()
        else -> null
    }

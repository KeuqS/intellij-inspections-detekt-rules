package by.overpas.detekt.intellij.jvm.general

import org.jetbrains.kotlin.analysis.api.symbols.KaClassKind
import org.jetbrains.kotlin.analysis.api.symbols.KaClassLikeSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaConstructorSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaFunctionSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedFunctionSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaVariableSymbol
import org.jetbrains.kotlin.analysis.api.types.KaClassType
import org.jetbrains.kotlin.analysis.api.types.KaFlexibleType
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.analysis.api.types.KaTypeParameterType

internal fun KaSymbol.apiStatusName(isQualified: Boolean = true): String =
    when (this) {
        is KaClassLikeSymbol -> classId?.asFqNameString() ?: name?.asString().orEmpty()

        is KaConstructorSymbol ->
            "${containingClassId?.run { shortClassName.asString() }.orEmpty()}(${parametersText(isQualified)})"

        is KaNamedFunctionSymbol -> "${name.asString()}(${parametersText(isQualified)})"

        is KaVariableSymbol -> name.asString()

        else -> ""
    }

internal fun KaSymbol.apiStatusKindName(): String =
    when ((this as? KaClassSymbol)?.classKind) {
        KaClassKind.ENUM_CLASS -> "enum"
        KaClassKind.ANNOTATION_CLASS -> "annotation"
        KaClassKind.INTERFACE -> "interface"
        KaClassKind.OBJECT, KaClassKind.COMPANION_OBJECT, KaClassKind.ANONYMOUS_OBJECT -> "object"
        KaClassKind.CLASS -> "class"
        null -> if (this is KaFunctionSymbol) "method" else "field"
    }

private fun KaFunctionSymbol.parametersText(isQualified: Boolean): String =
    valueParameters.joinToString(", ") { it.returnType.apiStatusText(isQualified) }

private fun KaType.apiStatusText(isQualified: Boolean): String =
    when (this) {
        is KaFlexibleType -> lowerBound.apiStatusText(isQualified)
        is KaClassType -> if (isQualified) classId.asFqNameString() else classId.shortClassName.asString()
        is KaTypeParameterType -> name.asString()
        else -> toString()
    }

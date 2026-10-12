package by.overpas.detekt.intellij.jvm.codematurity.markedforremoval

import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaConstructorSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaFunctionSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbol
import org.jetbrains.kotlin.analysis.api.symbols.markers.KaNamedSymbol

internal fun KaSymbol.presentableName(): String {
    val className = (this as? KaClassSymbol)?.classId?.asFqNameString()
    val functionName = (this as? KaFunctionSymbol)?.run { "${functionName()}(${parameterTypes().joinToString()})" }
    return className ?: functionName ?: (this as? KaNamedSymbol)?.run { name.asString() }.orEmpty()
}

private fun KaFunctionSymbol.functionName(): String =
    if (this is KaConstructorSymbol) {
        containingClassId?.run { shortClassName.asString() }.orEmpty()
    } else {
        (this as? KaNamedSymbol)?.run { name.asString() }.orEmpty()
    }

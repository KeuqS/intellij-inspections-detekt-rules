package by.overpas.detekt.intellij.jvm.general

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedFunctionSymbol

context(session: KaSession)
internal fun KaNamedFunctionSymbol.hasSignatureOf(other: KaNamedFunctionSymbol): Boolean =
    name == other.name &&
        valueParameters.size == other.valueParameters.size &&
        valueParameters.zip(other.valueParameters).all { (own, overridden) ->
            with(session) { own.returnType.semanticallyEquals(overridden.returnType) }
        }

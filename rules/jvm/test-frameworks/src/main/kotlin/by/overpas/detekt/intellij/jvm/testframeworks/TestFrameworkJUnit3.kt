package by.overpas.detekt.intellij.jvm.testframeworks

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedFunctionSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbolVisibility

context(session: KaSession)
internal fun KaNamedFunctionSymbol.hasJUnit3TestSignature(): Boolean =
    name.asString().startsWith("test") &&
        visibility == KaSymbolVisibility.PUBLIC &&
        receiverParameter == null &&
        valueParameters.isEmpty() &&
        with(session) { returnType.isUnitType }

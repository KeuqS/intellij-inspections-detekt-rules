package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaValueParameterSymbol
import org.jetbrains.kotlin.name.StandardClassIds

context(session: KaSession)
internal fun List<KaValueParameterSymbol>.logStringIndex(): Int? =
    when {
        this[0].returnType.classIdOrNull == StandardClassIds.String -> 1
        size >= 2 && this[1].returnType.classIdOrNull == StandardClassIds.String -> 2
        else -> null
    }

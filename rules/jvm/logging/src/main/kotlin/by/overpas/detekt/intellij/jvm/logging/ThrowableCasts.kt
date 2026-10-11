package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaClassKind
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.types.KaClassType
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.analysis.api.types.KaTypeParameterType
import org.jetbrains.kotlin.name.StandardClassIds

context(session: KaSession)
internal fun KaType.isCastableToThrowable(): Boolean =
    with(session) {
        val type = lowerBoundIfFlexible()
        val classKind = ((type as? KaClassType)?.symbol as? KaClassSymbol)?.classKind
        type is KaTypeParameterType ||
            type.isSubtypeOf(StandardClassIds.Throwable) ||
            builtinTypes.throwable.isSubtypeOf(type) ||
            classKind == KaClassKind.INTERFACE
    }

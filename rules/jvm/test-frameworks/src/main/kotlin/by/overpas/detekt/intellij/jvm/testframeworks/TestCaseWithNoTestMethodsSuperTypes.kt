package by.overpas.detekt.intellij.jvm.testframeworks

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaClassKind
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.types.KaClassType
import org.jetbrains.kotlin.name.StandardClassIds

internal fun KaClassSymbol.hasUnresolvedSuperTypes(): Boolean =
    superTypes.any { it !is KaClassType }

context(session: KaSession)
internal fun KaClassSymbol.superClass(): KaClassSymbol? =
    superTypes
        .mapNotNull { with(session) { it.expandedSymbol } }
        .firstOrNull { it.classKind == KaClassKind.CLASS && it.classId != StandardClassIds.Any }

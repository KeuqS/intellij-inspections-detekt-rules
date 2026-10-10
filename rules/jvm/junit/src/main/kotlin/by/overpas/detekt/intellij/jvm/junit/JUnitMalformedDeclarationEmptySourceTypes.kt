package by.overpas.detekt.intellij.jvm.junit

import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.NAVIGABLE_MAP
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.NAVIGABLE_SET
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.SORTED_MAP
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.SORTED_SET
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaClassKind
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbolModality
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbolVisibility
import org.jetbrains.kotlin.analysis.api.types.KaClassType
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.name.StandardClassIds

private val EMPTY_SOURCE_TYPES = setOf(
    StandardClassIds.String,
    StandardClassIds.Collection,
    StandardClassIds.MutableCollection,
    StandardClassIds.List,
    StandardClassIds.MutableList,
    StandardClassIds.Set,
    StandardClassIds.MutableSet,
    StandardClassIds.Map,
    StandardClassIds.MutableMap,
    SORTED_SET,
    NAVIGABLE_SET,
    SORTED_MAP,
    NAVIGABLE_MAP,
)

context(session: KaSession)
internal fun KaType.isEmptySourceType(): Boolean =
    with(session) {
        val type = lowerBound()
        type.isArrayOrPrimitiveArray || (type as? KaClassType)?.classId in EMPTY_SOURCE_TYPES ||
            type.isConstructibleCollection()
    }

context(session: KaSession)
private fun KaType.isConstructibleCollection(): Boolean =
    with(session) {
        val symbol = expandedSymbol
        symbol != null &&
            (isSubtypeOf(StandardClassIds.Collection) || isSubtypeOf(StandardClassIds.Map)) &&
            symbol.classKind == KaClassKind.CLASS &&
            symbol.modality != KaSymbolModality.ABSTRACT &&
            symbol.declaredMemberScope.constructors.any { constructor ->
                constructor.visibility == KaSymbolVisibility.PUBLIC &&
                    constructor.valueParameters.isEmpty()
            }
    }

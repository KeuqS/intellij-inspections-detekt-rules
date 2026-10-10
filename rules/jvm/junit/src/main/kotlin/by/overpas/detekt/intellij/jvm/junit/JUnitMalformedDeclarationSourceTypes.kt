package by.overpas.detekt.intellij.jvm.junit

import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.ARGUMENTS
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.BASE_STREAM
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.SEQUENCE
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedFunctionSymbol
import org.jetbrains.kotlin.analysis.api.types.KaClassType
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds

private val SOURCE_CONTAINERS = setOf(StandardClassIds.Iterable, StandardClassIds.Iterator, BASE_STREAM, SEQUENCE)

private val ITERATOR_NAME = Name.identifier("iterator")

context(session: KaSession)
internal fun KaType.sourceComponentType(): KaType? =
    with(session) {
        val type = lowerBound()
        type.arrayElementType ?: type.containerElementType() ?: type.iteratorElementType()
    }

context(session: KaSession)
private fun KaType.containerElementType(): KaType? =
    with(session) {
        val container = (sequenceOf(this@containerElementType) + allSupertypes)
            .map { it.lowerBound() }
            .filterIsInstance<KaClassType>()
            .firstOrNull { it.classId in SOURCE_CONTAINERS }
        container?.let { it.typeArguments.firstOrNull()?.type ?: builtinTypes.nullableAny }
    }

context(session: KaSession)
private fun KaType.iteratorElementType(): KaType? =
    with(session) {
        val callables = expandedSymbol?.run { memberScope.callables(ITERATOR_NAME) }.orEmpty()
        val hasIterator = callables.any { it is KaNamedFunctionSymbol && it.valueParameters.isEmpty() }
        builtinTypes.nullableAny.takeIf { hasIterator }
    }

context(session: KaSession)
internal fun KaType.isArgumentsLike(): Boolean =
    with(session) {
        val type = lowerBound()
        val element = type.arrayElementType
        type.isSubtypeOf(ARGUMENTS) || type.isAnyType || (element != null && element.lowerBound().isAnyType)
    }

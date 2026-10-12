package by.overpas.detekt.intellij.jvm.general.blockingmethodinnonblockingcontext

import org.jetbrains.kotlin.analysis.api.annotations.KaAnnotated
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName

internal class BlockingMethodInNonBlockingContextAnnotations(
    blocking: Set<String>,
    nonBlocking: Set<String>,
) {

    private val blockingIds = blocking.map { ClassId.topLevel(FqName(it)) }.toSet()

    private val nonBlockingIds = nonBlocking.map { ClassId.topLevel(FqName(it)) }.toSet()

    fun isBlocking(
        method: KaAnnotated,
        owner: KaClassSymbol?,
    ): Boolean =
        when {
            method.hasAny(blockingIds) -> true
            method.hasAny(nonBlockingIds) -> false
            else -> owner?.hasAny(blockingIds) == true
        }

    fun classify(
        method: KaAnnotated,
        owner: KaClassSymbol?,
    ): BlockingMethodInNonBlockingContextType =
        when {
            method.hasAny(nonBlockingIds) -> BlockingMethodInNonBlockingContextType.NON_BLOCKING
            method.hasAny(blockingIds) -> BlockingMethodInNonBlockingContextType.BLOCKING
            owner?.hasAny(nonBlockingIds) == true -> BlockingMethodInNonBlockingContextType.NON_BLOCKING
            else -> BlockingMethodInNonBlockingContextType.UNSURE
        }

    private fun KaAnnotated.hasAny(ids: Set<ClassId>): Boolean =
        annotations.any { it.classId in ids }
}

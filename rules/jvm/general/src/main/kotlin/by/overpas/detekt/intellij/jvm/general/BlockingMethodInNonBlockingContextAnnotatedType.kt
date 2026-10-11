package by.overpas.detekt.intellij.jvm.general

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.name.ClassId

internal class BlockingMethodInNonBlockingContextAnnotatedType(
    private val annotation: ClassId,
    private val isSuperClassChecked: Boolean = false,
) {

    context(session: KaSession)
    fun matches(type: KaType): Boolean =
        with(session) {
            val symbol = type.expandedSymbol
            type.annotations.any { it.classId == annotation } ||
                symbol?.run { annotations.any { it.classId == annotation } } == true ||
                (isSuperClassChecked && symbol?.run { superTypes.any { matches(it) } } == true)
        }
}

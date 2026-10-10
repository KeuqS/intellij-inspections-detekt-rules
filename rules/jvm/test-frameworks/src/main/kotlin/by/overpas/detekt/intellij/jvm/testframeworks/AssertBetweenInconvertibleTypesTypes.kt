package by.overpas.detekt.intellij.jvm.testframeworks

import org.jetbrains.kotlin.analysis.api.KaExperimentalApi
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.renderer.types.impl.KaTypeRendererForSource
import org.jetbrains.kotlin.analysis.api.types.KaClassType
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.types.Variance

@OptIn(KaExperimentalApi::class)
context(session: KaSession)
internal fun inconvertibleTypes(
    first: KaType,
    second: KaType,
): Pair<String, String>? {
    val left = first.comparableType()
    val right = second.comparableType()
    val leftClass = left?.let { with(session) { it.expandedSymbol } }
    val rightClass = right?.let { with(session) { it.expandedSymbol } }
    val isInconvertible = leftClass != null && rightClass != null && !leftClass.isConvertibleTo(rightClass)
    return if (isInconvertible) {
        with(session) {
            left.render(KaTypeRendererForSource.WITH_SHORT_NAMES, Variance.INVARIANT) to
                right.render(KaTypeRendererForSource.WITH_SHORT_NAMES, Variance.INVARIANT)
        }
    } else {
        null
    }
}

context(session: KaSession)
private fun KaType.comparableType(): KaClassType? {
    val type = with(session) { lowerBoundIfFlexible().withNullability(false) } as? KaClassType
    return type?.takeUnless { with(session) { it.isNothingType } }
}

package by.overpas.detekt.intellij.jvm.logging

internal class PartIntersection(private val cursors: Pair<PartCursor, PartCursor>) {

    fun length(): Int? {
        val (first, second) = cursors
        if (!first.hasNext || !second.hasNext) return 0
        return nextStep()?.let { (length, next) -> PartIntersection(next).length()?.plus(length) }
    }

    private fun nextStep(): PartStep? {
        val (first, second) = cursors
        return when {
            !first.isText -> PartStep(0, first.advance() to second)
            !second.isText -> PartStep(0, first to second.advance())
            first.current == second.current -> cursors.equalStep()
            first.isFirst && second.isFirst -> cursors.prefixStep()
            first.isLast && second.isLast -> cursors.suffixStep()
            else -> cursors.containedStep()
        }
    }
}

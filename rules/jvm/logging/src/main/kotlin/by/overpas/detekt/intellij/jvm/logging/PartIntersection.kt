package by.overpas.detekt.intellij.jvm.logging

internal class PartIntersection(private val iterators: Pair<PartIterator, PartIterator>) {

    fun length(): Int? {
        val (first, second) = iterators
        var intersection = 0
        var isMismatch = false
        while (!isMismatch && first.hasNext && second.hasNext) {
            val step = nextStep()
            isMismatch = step == null
            intersection += step ?: 0
        }
        return intersection.takeIf { !isMismatch }
    }

    private fun nextStep(): Int? {
        val (first, second) = iterators
        return when {
            !first.isText -> 0.also { first.move() }
            !second.isText -> 0.also { second.move() }
            first.current == second.current -> iterators.equalStep()
            first.isFirst && second.isFirst -> iterators.prefixStep()
            first.isLast && second.isLast -> iterators.suffixStep()
            else -> iterators.containedStep()
        }
    }
}

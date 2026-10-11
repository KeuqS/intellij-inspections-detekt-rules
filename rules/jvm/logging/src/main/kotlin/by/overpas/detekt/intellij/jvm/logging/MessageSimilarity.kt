package by.overpas.detekt.intellij.jvm.logging

private const val MAX_PART_COUNT = 10

internal class MessageSimilarity(private val minTextLength: Int) {

    fun isSimilar(
        first: List<PartHolder>?,
        second: List<PartHolder>?,
    ): Boolean =
        !first.isNullOrEmpty() &&
            !second.isNullOrEmpty() &&
            first.hasSameCallArgumentsAs(second) &&
            first.size < MAX_PART_COUNT &&
            second.size < MAX_PART_COUNT &&
            first.hasSameShapeAs(second) &&
            PartIntersection(PartIterator(first) to PartIterator(second)).length()?.let { it >= minTextLength } == true
}

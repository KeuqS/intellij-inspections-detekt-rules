package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.psi.KtCallExpression

internal class SimilarPairs(private val similarity: MessageSimilarity) {

    fun find(group: List<MessageLog>): List<Pair<KtCallExpression, KtCallExpression>> {
        val reported = mutableSetOf<Int>()
        val result = mutableListOf<Pair<KtCallExpression, KtCallExpression>>()
        for (firstIndex in group.indices) {
            for (secondIndex in firstIndex + 1..group.lastIndex) {
                val (firstCall, firstParts) = group[firstIndex]
                val (secondCall, secondParts) = group[secondIndex]
                if (similarity.isSimilar(firstParts, secondParts) && !firstCall.isInSequenceWith(secondCall)) {
                    if (reported.add(firstIndex)) result += firstCall to secondCall
                    if (reported.add(secondIndex)) result += secondCall to firstCall
                }
            }
        }
        return result
    }
}

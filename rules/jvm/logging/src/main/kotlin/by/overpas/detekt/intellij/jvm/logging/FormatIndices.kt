package by.overpas.detekt.intellij.jvm.logging

internal fun List<MatchResult>.formatIndexCount(): Int? {
    val explicitIndices = mapNotNull { it.groupValues[1].takeIf(String::isNotEmpty)?.run { dropLast(1).toInt() } }
    val ordinaryCount = count { it.groupValues[1].isEmpty() && '<' !in it.groupValues[2] }
    val indices = buildSet {
        addAll(explicitIndices)
        addAll(1..ordinaryCount)
    }
    val count = indices.maxOrNull() ?: 0
    return count.takeIf { (1..count).all { index -> index in indices } }
}

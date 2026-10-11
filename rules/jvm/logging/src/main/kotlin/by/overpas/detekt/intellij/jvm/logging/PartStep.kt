package by.overpas.detekt.intellij.jvm.logging

internal data class PartStep(
    val length: Int,
    val cursors: Pair<PartCursor, PartCursor>,
)

package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.psi.KtCallExpression

internal data class MessageLog(
    val call: KtCallExpression,
    val parts: List<PartHolder>?,
)

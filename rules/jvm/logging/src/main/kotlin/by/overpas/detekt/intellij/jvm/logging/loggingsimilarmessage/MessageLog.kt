package by.overpas.detekt.intellij.jvm.logging.loggingsimilarmessage

import by.overpas.detekt.intellij.jvm.logging.PartHolder
import org.jetbrains.kotlin.psi.KtCallExpression

internal data class MessageLog(
    val call: KtCallExpression,
    val parts: List<PartHolder>?,
)

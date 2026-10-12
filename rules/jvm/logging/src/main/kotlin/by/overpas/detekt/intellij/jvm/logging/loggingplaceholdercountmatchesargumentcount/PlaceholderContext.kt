package by.overpas.detekt.intellij.jvm.logging.loggingplaceholdercountmatchesargumentcount

import by.overpas.detekt.intellij.jvm.logging.PartHolder
import org.jetbrains.kotlin.psi.KtExpression

internal data class PlaceholderContext(
    val placeholderArguments: List<KtExpression>,
    val logStringArgument: KtExpression,
    val parts: List<PartHolder>,
    val loggerType: PlaceholderLoggerType,
    val isLastArgumentException: Boolean,
    val isLastArgumentSupplier: Boolean,
)

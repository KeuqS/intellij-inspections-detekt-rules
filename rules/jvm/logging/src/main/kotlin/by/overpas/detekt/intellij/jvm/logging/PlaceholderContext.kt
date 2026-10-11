package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.psi.KtExpression

internal data class PlaceholderContext(
    val placeholderArguments: List<KtExpression>,
    val logStringArgument: KtExpression,
    val parts: List<PartHolder>,
    val loggerType: PlaceholderLoggerType,
    val isLastArgumentException: Boolean,
    val isLastArgumentSupplier: Boolean,
)

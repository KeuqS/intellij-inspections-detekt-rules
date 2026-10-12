package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.psi.KtEscapeStringTemplateEntry
import org.jetbrains.kotlin.psi.KtStringTemplateEntry
import org.jetbrains.kotlin.psi.KtStringTemplateExpression

internal val KtStringTemplateExpression.literalText: String
    get() = entries.joinToString("") { it.literalText }

internal val KtStringTemplateEntry.literalText: String
    get() = (this as? KtEscapeStringTemplateEntry)?.unescapedValue ?: text

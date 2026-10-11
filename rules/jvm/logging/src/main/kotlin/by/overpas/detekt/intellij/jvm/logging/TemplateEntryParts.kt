package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.psi.KtStringTemplateEntryWithExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression

internal val KtStringTemplateExpression.entryParts: List<TemplatePart>
    get() = entries.map { entry ->
        if (entry is KtStringTemplateEntryWithExpression) {
            TemplatePart(entry.expression, isLiteral = false)
        } else {
            TemplatePart(null, isLiteral = true)
        }
    }

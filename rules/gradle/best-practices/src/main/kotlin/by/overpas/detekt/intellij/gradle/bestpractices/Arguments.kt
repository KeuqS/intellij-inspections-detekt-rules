package by.overpas.detekt.intellij.gradle.bestpractices

import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentList

internal fun KtValueArgument.name(): String? =
    getArgumentName()?.run { asName.identifier }

internal fun KtValueArgumentList.argument(
    name: String,
    index: Int,
): KtExpression? {
    val argument = arguments.find { it.name() == name }
        ?: arguments.getOrNull(index)?.takeUnless { it.isNamed() }
    return argument?.getArgumentExpression()
}

internal fun KtExpression.stringValue(): String? =
    (this as? KtStringTemplateExpression)?.entries
        ?.takeIf { entries -> entries.all { it is KtLiteralStringTemplateEntry } }
        ?.joinToString("") { it.text }

internal fun KtExpression?.versionSuffix(): String? =
    if (this == null) "" else stringValue()?.let { ":$it" }

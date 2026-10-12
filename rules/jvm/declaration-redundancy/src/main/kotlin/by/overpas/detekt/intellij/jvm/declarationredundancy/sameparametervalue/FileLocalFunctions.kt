package by.overpas.detekt.intellij.jvm.declarationredundancy.sameparametervalue

import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.parents

private val EXTERNALLY_USED_MODIFIERS = listOf(
    KtTokens.OVERRIDE_KEYWORD,
    KtTokens.OPERATOR_KEYWORD,
    KtTokens.EXTERNAL_KEYWORD,
)

internal fun KtNamedFunction.isFileLocal(): Boolean =
    !isLocal &&
        name != null &&
        valueParameters.isNotEmpty() &&
        EXTERNALLY_USED_MODIFIERS.none { hasModifier(it) } &&
        (
            hasModifier(KtTokens.PRIVATE_KEYWORD) ||
                parents.any { it is KtClassOrObject && (it.isLocal || it.hasModifier(KtTokens.PRIVATE_KEYWORD)) }
            )

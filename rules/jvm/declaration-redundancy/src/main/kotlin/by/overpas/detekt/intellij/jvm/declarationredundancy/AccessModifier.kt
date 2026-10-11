package by.overpas.detekt.intellij.jvm.declarationredundancy

import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtModifierListOwner

internal enum class AccessModifier(private val key: String) {
    PUBLIC("public"),
    PROTECTED("protected"),
    PACKAGE_LOCAL("package-private"),
    PRIVATE("private"),
    ;

    companion object {
        fun fromKey(key: String): AccessModifier =
            entries.firstOrNull { it.key == key } ?: PROTECTED
    }
}

internal fun KtModifierListOwner.accessModifier(): AccessModifier =
    when {
        hasModifier(KtTokens.PRIVATE_KEYWORD) -> AccessModifier.PRIVATE
        hasModifier(KtTokens.PROTECTED_KEYWORD) -> AccessModifier.PROTECTED
        else -> AccessModifier.PUBLIC
    }

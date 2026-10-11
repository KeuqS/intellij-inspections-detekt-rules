package by.overpas.detekt.intellij.android.kotlin.illegalidentifier

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtPsiUtil
import org.jetbrains.kotlin.resolve.jvm.checkers.isValidDalvikIdentifier

internal fun PsiElement.isIllegalDalvikIdentifier(): Boolean =
    node?.elementType == KtTokens.IDENTIFIER &&
        text.startsWith('`') &&
        text.endsWith('`') &&
        KtPsiUtil.unquoteIdentifier(text).let { name -> name.isNotEmpty() && !isValidDalvikIdentifier(name) }

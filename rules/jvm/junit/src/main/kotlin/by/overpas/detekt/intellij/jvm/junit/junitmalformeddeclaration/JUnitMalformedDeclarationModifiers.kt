package by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration

import by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration.MalformedClassIds.JVM_FIELD
import by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration.MalformedClassIds.JVM_STATIC
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtModifierListOwner
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject

internal fun KtModifierListOwner.isPrivateMember(): Boolean =
    hasModifier(KtTokens.PRIVATE_KEYWORD)

internal fun KtModifierListOwner.isPublicMember(): Boolean =
    !isPrivateMember() && !hasModifier(KtTokens.PROTECTED_KEYWORD)

internal fun KtModifierListOwner.isSuspend(): Boolean =
    hasModifier(KtTokens.SUSPEND_KEYWORD)

private fun KtCallableDeclaration.isInObject(): Boolean =
    (containingClassOrObject as? KtObjectDeclaration)?.isObjectLiteral() == false

context(session: KaSession)
internal fun KtCallableDeclaration.isJvmStatic(): Boolean =
    isInObject() && JVM_STATIC in annotationIds()

internal fun KtProperty.isFieldStatic(): Boolean =
    isInObject() || hasModifier(KtTokens.CONST_KEYWORD)

context(session: KaSession)
internal fun KtProperty.isFieldPublic(): Boolean =
    isPublicMember() &&
        (hasModifier(KtTokens.LATEINIT_KEYWORD) || hasModifier(KtTokens.CONST_KEYWORD) || JVM_FIELD in annotationIds())

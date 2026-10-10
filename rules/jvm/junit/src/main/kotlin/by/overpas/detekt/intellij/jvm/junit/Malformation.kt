package by.overpas.detekt.intellij.jvm.junit

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtNamedDeclaration

internal data class Malformation(
    val anchor: PsiElement,
    val message: String,
)

internal fun KtNamedDeclaration.malformation(message: String): Malformation =
    Malformation(nameIdentifier ?: this, message)

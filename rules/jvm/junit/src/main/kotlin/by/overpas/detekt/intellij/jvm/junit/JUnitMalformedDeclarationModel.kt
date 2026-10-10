package by.overpas.detekt.intellij.jvm.junit

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtNamedDeclaration

internal data class Malformation(
    val anchor: PsiElement,
    val message: String,
)

internal data class MalformedSettings(
    val ignorableAnnotations: Set<String>,
    val areSuspendFunctionsAllowed: Boolean,
)

internal fun KtNamedDeclaration.malformation(message: String): Malformation =
    Malformation(nameIdentifier ?: this, message)

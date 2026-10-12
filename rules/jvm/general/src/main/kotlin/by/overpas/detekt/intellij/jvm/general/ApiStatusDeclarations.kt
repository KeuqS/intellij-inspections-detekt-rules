package by.overpas.detekt.intellij.jvm.general

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtConstructor
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty

internal fun KtDeclaration.isApiStatusDeclaration(): Boolean =
    when (this) {
        is KtClassOrObject -> (this as? KtObjectDeclaration)?.isObjectLiteral() != true
        is KtNamedFunction -> !isLocal
        is KtProperty -> !isLocal
        is KtConstructor<*> -> true
        is KtParameter -> hasValOrVar()
        else -> false
    }

internal fun KtDeclaration.apiStatusAnchor(): PsiElement =
    when (this) {
        is KtConstructor<*> -> getConstructorKeyword() ?: this
        is KtNamedDeclaration -> nameIdentifier ?: this
        else -> this
    }

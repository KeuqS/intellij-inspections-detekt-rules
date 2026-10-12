package by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.annotations.KaAnnotation
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject

context(session: KaSession, provided: ProvidedParameters)
internal fun KaAnnotation.methodSourceMalformations(function: KtNamedFunction): List<Malformation> {
    val owner = function.containingClassOrObject as? KtClass
    val names = stringValues().ifEmpty { listOfNotNull(function.name) }
        .filter { it.isNotBlank() && '#' !in it && '(' !in it }
    return names.mapNotNull { sourceName ->
        owner?.methodSourceMessage(sourceName)?.let { Malformation(anchorFor(sourceName) ?: function, it) }
    }
}

private fun KaAnnotation.anchorFor(sourceName: String): PsiElement? =
    psi?.let { call ->
        val literal = call.collectDescendantsOfType<KtStringTemplateExpression> { literal ->
            literal.text.removeSurrounding("\"") == sourceName
        }
        literal.firstOrNull() ?: call
    }

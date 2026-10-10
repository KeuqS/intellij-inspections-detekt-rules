package by.overpas.detekt.intellij.jvm.junit

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.annotations.KaAnnotation
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject

context(session: KaSession)
internal fun KtNamedFunction.methodSourceMalformations(
    annotation: KaAnnotation,
    provided: ProvidedParameters,
): List<Malformation> {
    val owner = containingClassOrObject as? KtClass
    val names = annotation.stringValues().ifEmpty { listOfNotNull(name) }
        .filter { it.isNotBlank() && '#' !in it && '(' !in it }
    return names.mapNotNull { sourceName ->
        owner?.methodSourceMessage(
            sourceName,
            provided,
        )?.let { Malformation(annotation.anchorFor(sourceName) ?: this, it) }
    }
}

private fun KaAnnotation.anchorFor(sourceName: String): PsiElement? =
    psi?.let { call ->
        call.collectDescendantsOfType<KtStringTemplateExpression> { it.text.removeSurrounding("\"") == sourceName }
            .firstOrNull() ?: call
    }

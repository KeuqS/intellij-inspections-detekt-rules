package by.overpas.detekt.intellij.kotlin.redundantconstructs.removeredundantqualifiername

import com.intellij.psi.util.PsiTreeUtil
import org.jetbrains.kotlin.kdoc.psi.api.KDoc
import org.jetbrains.kotlin.psi.KtElement

internal fun KtElement.kDocFreeElements(): List<KtElement> =
    if (PsiTreeUtil.findChildOfType(this, KDoc::class.java) == null) {
        listOf(this)
    } else {
        children.filterIsInstance<KtElement>().flatMap { it.kDocFreeElements() }
    }

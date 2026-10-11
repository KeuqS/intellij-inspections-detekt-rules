package by.overpas.detekt.intellij.jvm.codematurity

import com.intellij.psi.PsiMethod
import org.jetbrains.kotlin.analysis.api.symbols.KaFunctionSymbol
import org.jetbrains.kotlin.psi.KtFunction

internal fun KaFunctionSymbol.parameterTypes(): List<String> =
    when (val declaration = psi) {
        is PsiMethod -> declaration.parameterList.parameters.map { it.type.canonicalText }
        is KtFunction -> declaration.valueParameters.map { it.typeReference?.text.orEmpty() }
        else -> emptyList()
    }

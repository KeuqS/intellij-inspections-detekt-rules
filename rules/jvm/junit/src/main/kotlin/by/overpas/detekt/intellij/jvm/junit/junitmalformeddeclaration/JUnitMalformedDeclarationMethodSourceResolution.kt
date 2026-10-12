package by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedClassSymbol
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtClass

context(session: KaSession, provided: ProvidedParameters)
internal fun KtClass.methodSourceMessage(sourceName: String): String? =
    with(session) {
        val ownerSymbol = classSymbol as? KaNamedClassSymbol
        val candidates = ownerSymbol?.sourceCandidates(Name.identifier(sourceName)).orEmpty()
        val candidate = candidates.firstOrNull { it.symbol.valueParameters.isEmpty() } ?: candidates.firstOrNull()
        val isAbstract = isInterface() || hasModifier(KtTokens.ABSTRACT_KEYWORD)
        when {
            ownerSymbol == null -> null

            candidate == null -> "Cannot resolve target method source: '$sourceName'".takeUnless { isAbstract }

            !candidate.isStatic && !isAbstract && !ownerSymbol.isTestInstancePerClass() ->
                "Method source '$sourceName' must be static"

            candidate.symbol.valueParameters.isNotEmpty() && !hasResolverExtensionField() ->
                "Method source '$sourceName' should have no parameters"

            else -> candidate.returnTypeMessage(sourceName)
        }
    }

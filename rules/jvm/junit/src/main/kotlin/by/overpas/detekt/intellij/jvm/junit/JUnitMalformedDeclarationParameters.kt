package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtNamedFunction

context(session: KaSession, settings: MalformedSettings)
internal fun KtNamedFunction.invalidParameters(requirement: ParameterRequirement?): List<String> =
    with(session) {
        val isSkipped = requirement == null ||
            valueParameters.isEmpty() ||
            (requirement == ParameterRequirement.JUPITER_TEST && symbol.hasArgumentsSource()) ||
            (requirement != ParameterRequirement.NONE && inParameterResolverContext())
        if (isSkipped || requirement == null) {
            emptyList()
        } else {
            valueParameters.filterNot { parameter ->
                val parameterSymbol = parameter.symbol
                val isInjected = requirement.injects(parameterSymbol.returnType)
                val isResolved = parameterSymbol.hasParameterResolver() || isInjected
                parameter.isIgnorable() || (requirement != ParameterRequirement.NONE && isResolved)
            }.mapNotNull { it.name }
        }
    }

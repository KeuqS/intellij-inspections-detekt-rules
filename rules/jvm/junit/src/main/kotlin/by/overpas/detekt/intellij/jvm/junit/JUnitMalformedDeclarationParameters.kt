package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtNamedFunction

context(session: KaSession)
internal fun KtNamedFunction.invalidParameters(
    requirement: ParameterRequirement?,
    settings: MalformedSettings,
): List<String> =
    with(session) {
        val isSkipped = requirement == null ||
            valueParameters.isEmpty() ||
            requirement == ParameterRequirement.JUPITER_TEST && symbol.hasArgumentsSource() ||
            requirement != ParameterRequirement.NONE && inParameterResolverContext()
        if (isSkipped || requirement == null) {
            emptyList()
        } else {
            valueParameters.filterNot { parameter ->
                parameter.isIgnorable(settings) ||
                    requirement != ParameterRequirement.NONE &&
                    (parameter.symbol.hasParameterResolver() || parameter.symbol.returnType.isInjectable(requirement))
            }.mapNotNull { it.name }
        }
    }

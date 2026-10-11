package by.overpas.detekt.intellij.jvm.declarationredundancy.sameparametervalue

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtNamedFunction

@IntellijInspection("SameParameterValue")
class SameParameterValue(config: Config) :
    Rule(
        config,
        "Every call of a function passes the same constant value to a parameter. " +
            "Remove the parameter and use the value in the function.",
    ),
    RequiresAnalysisApi {

    @Configuration(
        "The most visible function visibility to report: 'public', 'protected', 'package-private' or 'private'.",
    )
    private val highestModifier: String by config("protected")

    @Configuration("The minimal number of function usages to report a parameter.")
    private val minimalUsageCount: Int by config(1)

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        val isReported = function.isFileLocal() &&
            function.accessModifier() >= AccessModifier.fromKey(highestModifier)
        val calls = if (isReported) analyze(function) { function.callArguments() } else null
        if (calls == null || (minimalUsageCount != 0 && calls.size < minimalUsageCount)) return
        val values = ParameterValues(function.valueParameters.size)
        calls.forEach(values::add)
        function.valueParameters.zip(values.presentations).forEach { (parameter, value) ->
            if (value != null) {
                report(
                    Finding(
                        Entity.atName(parameter),
                        "Value of parameter '${parameter.name.orEmpty()}' is always '$value'",
                    ),
                )
            }
        }
    }
}

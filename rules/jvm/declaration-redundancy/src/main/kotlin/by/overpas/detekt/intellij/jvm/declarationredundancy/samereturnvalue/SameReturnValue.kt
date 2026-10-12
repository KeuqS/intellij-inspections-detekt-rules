package by.overpas.detekt.intellij.jvm.declarationredundancy.samereturnvalue

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtNamedFunction

@IntellijInspection("SameReturnValue")
class SameReturnValue(config: Config) :
    Rule(
        config,
        "A function that cannot be overridden returns the same constant value from every `return`. " +
            "Return nothing or make the value a constant.",
    ),
    RequiresAnalysisApi {

    override fun visitNamedFunction(function: KtNamedFunction) {
        super.visitNamedFunction(function)
        val name = function.name
        if (name == null || function.isLocal || !function.hasBody()) return
        val value = analyze(function) { function.sameReturnValue() } ?: return
        report(Finding(Entity.atName(function), "Method '$name()' always returns '$value'"))
    }
}

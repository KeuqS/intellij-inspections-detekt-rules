package by.overpas.detekt.intellij.jvm.errorhandling

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import org.jetbrains.kotlin.psi.KtReturnExpression

@IntellijInspection("ReturnFromFinallyBlock")
class ReturnFromFinallyBlock(config: Config) :
    Rule(
        config,
        "A `return` leaves a `finally` block. It hides the exception that the `try` block throws and " +
            "replaces the result of the `try` block. Move the `return` out of the `finally` block.",
    ) {

    override fun visitReturnExpression(expression: KtReturnExpression) {
        super.visitReturnExpression(expression)
        if (expression.leavesFinallyBlock()) {
            report(Finding(Entity.from(expression), "'return' inside 'finally' block"))
        }
    }
}

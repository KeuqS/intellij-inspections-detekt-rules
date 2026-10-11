package by.overpas.detekt.intellij.jvm.errorhandling

import by.overpas.detekt.intellij.IntellijInspectionGroup
import by.overpas.detekt.intellij.jvm.errorhandling.returnfromfinallyblock.ReturnFromFinallyBlock
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider

@IntellijInspectionGroup("group.names.error.handling")
class ErrorHandlingRuleSetProvider : RuleSetProvider {

    override val ruleSetId = RuleSetId("intellij-jvm-error-handling")

    override fun instance() =
        RuleSet(
            ruleSetId,
            listOf(
                ::ReturnFromFinallyBlock,
            ),
        )
}

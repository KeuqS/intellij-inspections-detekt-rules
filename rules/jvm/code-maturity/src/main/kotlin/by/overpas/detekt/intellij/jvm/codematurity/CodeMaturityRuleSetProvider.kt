package by.overpas.detekt.intellij.jvm.codematurity

import by.overpas.detekt.intellij.IntellijInspectionGroup
import by.overpas.detekt.intellij.jvm.codematurity.markedforremoval.MarkedForRemoval
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider

@IntellijInspectionGroup("group.names.code.maturity.issues")
class CodeMaturityRuleSetProvider : RuleSetProvider {

    override val ruleSetId = RuleSetId("intellij-jvm-code-maturity")

    override fun instance() =
        RuleSet(
            ruleSetId,
            listOf(
                ::MarkedForRemoval,
            ),
        )
}

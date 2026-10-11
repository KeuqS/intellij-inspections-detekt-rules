package by.overpas.detekt.intellij.jvm.declarationredundancy

import by.overpas.detekt.intellij.IntellijInspectionGroup
import by.overpas.detekt.intellij.jvm.declarationredundancy.sameparametervalue.SameParameterValue
import by.overpas.detekt.intellij.jvm.declarationredundancy.samereturnvalue.SameReturnValue
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider

@IntellijInspectionGroup("group.names.declaration.redundancy")
class DeclarationRedundancyRuleSetProvider : RuleSetProvider {

    override val ruleSetId = RuleSetId("intellij-jvm-declaration-redundancy")

    override fun instance() =
        RuleSet(
            ruleSetId,
            listOf(
                ::SameParameterValue,
                ::SameReturnValue,
            ),
        )
}

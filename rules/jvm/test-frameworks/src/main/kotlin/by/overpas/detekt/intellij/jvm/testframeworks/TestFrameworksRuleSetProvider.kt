package by.overpas.detekt.intellij.jvm.testframeworks

import by.overpas.detekt.intellij.IntellijInspectionGroup
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider

@IntellijInspectionGroup("jvm.inspections.test.frameworks.group.name")
class TestFrameworksRuleSetProvider : RuleSetProvider {

    override val ruleSetId = RuleSetId("intellij-jvm-test-frameworks")

    override fun instance() =
        RuleSet(
            ruleSetId,
            listOf(),
        )
}

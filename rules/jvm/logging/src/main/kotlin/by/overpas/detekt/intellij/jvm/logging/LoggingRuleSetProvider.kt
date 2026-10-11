package by.overpas.detekt.intellij.jvm.logging

import by.overpas.detekt.intellij.IntellijInspectionGroup
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider

@IntellijInspectionGroup("jvm.inspections.logging.frameworks.group.name")
class LoggingRuleSetProvider : RuleSetProvider {

    override val ruleSetId = RuleSetId("intellij-jvm-logging")

    override fun instance() =
        RuleSet(
            ruleSetId,
            listOf(
                ::LogStatementNotGuardedByLogCondition,
                ::LoggingConditionDisagreesWithLogLevelStatement,
                ::LoggingGuardedByCondition,
                ::LoggingPlaceholderCountMatchesArgumentCount,
                ::LoggingSimilarMessage,
                ::LoggingStringTemplateAsArgument,
            ),
        )
}

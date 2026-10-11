package by.overpas.detekt.intellij.jvm.logging

import by.overpas.detekt.intellij.IntellijInspectionGroup
import by.overpas.detekt.intellij.jvm.logging.loggingconditiondisagreeswithloglevelstatement.LoggingConditionDisagreesWithLogLevelStatement
import by.overpas.detekt.intellij.jvm.logging.loggingguardedbycondition.LoggingGuardedByCondition
import by.overpas.detekt.intellij.jvm.logging.loggingplaceholdercountmatchesargumentcount.LoggingPlaceholderCountMatchesArgumentCount
import by.overpas.detekt.intellij.jvm.logging.loggingsimilarmessage.LoggingSimilarMessage
import by.overpas.detekt.intellij.jvm.logging.loggingstringtemplateasargument.LoggingStringTemplateAsArgument
import by.overpas.detekt.intellij.jvm.logging.logstatementnotguardedbylogcondition.LogStatementNotGuardedByLogCondition
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

package by.overpas.detekt.intellij.kotlin.otherproblems

import by.overpas.detekt.intellij.IntellijInspectionGroup
import by.overpas.detekt.intellij.kotlin.otherproblems.convertargumenttoset.ConvertArgumentToSet
import by.overpas.detekt.intellij.kotlin.otherproblems.deprecatedcallableaddreplacewith.DeprecatedCallableAddReplaceWith
import by.overpas.detekt.intellij.kotlin.otherproblems.enumvaluessoftdeprecate.EnumValuesSoftDeprecate
import by.overpas.detekt.intellij.kotlin.otherproblems.enumvaluestoplevelfunctionsoftdeprecate.EnumValuesTopLevelFunctionSoftDeprecate
import by.overpas.detekt.intellij.kotlin.otherproblems.floatingpointliteralprecision.FloatingPointLiteralPrecision
import by.overpas.detekt.intellij.kotlin.otherproblems.migratediagnosticsuppression.MigrateDiagnosticSuppression
import by.overpas.detekt.intellij.kotlin.otherproblems.replacewithenummap.ReplaceWithEnumMap
import by.overpas.detekt.intellij.kotlin.otherproblems.replacewithstringbuilderappendrange.ReplaceWithStringBuilderAppendRange
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider

@IntellijInspectionGroup("group.names.other.problems")
class OtherProblemsRuleSetProvider : RuleSetProvider {

    override val ruleSetId = RuleSetId("intellij-kotlin-other-problems")

    override fun instance() =
        RuleSet(
            ruleSetId,
            listOf(
                ::ConvertArgumentToSet,
                ::DeprecatedCallableAddReplaceWith,
                ::EnumValuesSoftDeprecate,
                ::EnumValuesTopLevelFunctionSoftDeprecate,
                ::FloatingPointLiteralPrecision,
                ::MigrateDiagnosticSuppression,
                ::ReplaceWithEnumMap,
                ::ReplaceWithStringBuilderAppendRange,
            ),
        )
}

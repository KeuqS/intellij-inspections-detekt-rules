package by.overpas.detekt.intellij.jvm.general

import by.overpas.detekt.intellij.IntellijInspectionGroup
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider

@IntellijInspectionGroup("jvm.inspections.group.name")
class GeneralRuleSetProvider : RuleSetProvider {

    override val ruleSetId = RuleSetId("intellij-jvm-general")

    override fun instance() =
        RuleSet(
            ruleSetId,
            listOf(
                ::MissingDeprecatedAnnotationOnScheduledForRemovalApi,
                ::MustAlreadyBeRemovedApi,
                ::NonExtendableApiUsage,
                ::OverrideOnly,
                ::UnstableApiUsage,
                ::UnstableTypeUsedInSignature,
                ::UsagesOfObsoleteApi,
            ),
        )
}

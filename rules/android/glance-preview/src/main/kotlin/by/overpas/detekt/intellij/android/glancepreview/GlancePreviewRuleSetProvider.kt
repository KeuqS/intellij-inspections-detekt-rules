package by.overpas.detekt.intellij.android.glancepreview

import by.overpas.detekt.intellij.IntellijInspectionGroup
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider

@IntellijInspectionGroup("inspection.group.name")
class GlancePreviewRuleSetProvider : RuleSetProvider {

    override val ruleSetId = RuleSetId("intellij-android-glance-preview")

    override fun instance() =
        RuleSet(
            ruleSetId,
            emptyList(),
        )
}

package by.overpas.detekt.intellij.android.weartilepreview

import by.overpas.detekt.intellij.IntellijInspectionGroup
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider

@IntellijInspectionGroup("inspection.group.name")
class WearTilePreviewRuleSetProvider : RuleSetProvider {

    override val ruleSetId = RuleSetId("intellij-android-wear-tile-preview")

    override fun instance() =
        RuleSet(
            ruleSetId,
            emptyList(),
        )
}

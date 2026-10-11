package by.overpas.detekt.intellij.android.composepreview

import by.overpas.detekt.intellij.IntellijInspectionGroup
import by.overpas.detekt.intellij.android.composepreview.composepreviewneedscomposableannotation.ComposePreviewNeedsComposableAnnotation
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider

@IntellijInspectionGroup("inspection.group.name")
class ComposePreviewRuleSetProvider : RuleSetProvider {

    override val ruleSetId = RuleSetId("intellij-android-compose-preview")

    override fun instance() =
        RuleSet(
            ruleSetId,
            listOf(
                ::ComposePreviewNeedsComposableAnnotation,
            ),
        )
}

package by.overpas.detekt.intellij.android.kotlin

import by.overpas.detekt.intellij.IntellijInspectionGroup
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider

@IntellijInspectionGroup("Kotlin Android")
class KotlinAndroidRuleSetProvider : RuleSetProvider {

    override val ruleSetId = RuleSetId("intellij-android-kotlin")

    override fun instance() =
        RuleSet(
            ruleSetId,
            emptyList(),
        )
}

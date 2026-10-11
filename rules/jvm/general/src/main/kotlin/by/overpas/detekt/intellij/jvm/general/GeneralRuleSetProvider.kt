package by.overpas.detekt.intellij.jvm.general

import by.overpas.detekt.intellij.IntellijInspectionGroup
import by.overpas.detekt.intellij.jvm.general.blockingmethodinnonblockingcontext.BlockingMethodInNonBlockingContext
import by.overpas.detekt.intellij.jvm.general.missingdeprecatedannotationonscheduledforremovalapi.MissingDeprecatedAnnotationOnScheduledForRemovalApi
import by.overpas.detekt.intellij.jvm.general.mustalreadyberemovedapi.MustAlreadyBeRemovedApi
import by.overpas.detekt.intellij.jvm.general.nonextendableapiusage.NonExtendableApiUsage
import by.overpas.detekt.intellij.jvm.general.overrideonly.OverrideOnly
import by.overpas.detekt.intellij.jvm.general.serializablehasserialversionuidfield.SerializableHasSerialVersionUIDField
import by.overpas.detekt.intellij.jvm.general.suppressionannotation.SuppressionAnnotation
import by.overpas.detekt.intellij.jvm.general.systemgetproperty.SystemGetProperty
import by.overpas.detekt.intellij.jvm.general.threadrun.ThreadRun
import by.overpas.detekt.intellij.jvm.general.unstableapiusage.UnstableApiUsage
import by.overpas.detekt.intellij.jvm.general.unstabletypeusedinsignature.UnstableTypeUsedInSignature
import by.overpas.detekt.intellij.jvm.general.urlhashcode.UrlHashCode
import by.overpas.detekt.intellij.jvm.general.usagesofobsoleteapi.UsagesOfObsoleteApi
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
                ::BlockingMethodInNonBlockingContext,
                ::MissingDeprecatedAnnotationOnScheduledForRemovalApi,
                ::MustAlreadyBeRemovedApi,
                ::NonExtendableApiUsage,
                ::OverrideOnly,
                ::SerializableHasSerialVersionUIDField,
                ::SuppressionAnnotation,
                ::SystemGetProperty,
                ::ThreadRun,
                ::UnstableApiUsage,
                ::UnstableTypeUsedInSignature,
                ::UrlHashCode,
                ::UsagesOfObsoleteApi,
            ),
        )
}

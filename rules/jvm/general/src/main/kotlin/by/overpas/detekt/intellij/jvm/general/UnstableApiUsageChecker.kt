package by.overpas.detekt.intellij.jvm.general

import org.jetbrains.kotlin.analysis.api.KaSession

internal class UnstableApiUsageChecker(
    private val matcher: ApiStatusAnnotationMatcher,
    private val signatureMatcher: ApiStatusSignatureMatcher,
    private val isInsideImportsIgnored: Boolean,
    private val isApiDeclaredInThisProjectIgnored: Boolean,
) {

    context(session: KaSession)
    fun message(usage: ApiStatusUsage): String? {
        val target = usage.target
        val isOverride = usage.kind == ApiStatusUsage.Kind.OVERRIDE
        val isSkipped = (usage.kind == ApiStatusUsage.Kind.IMPORT && isInsideImportsIgnored) ||
            (isApiDeclaredInThisProjectIgnored && !target.isApiStatusLibrary())
        val annotated = if (isSkipped) null else matcher.findAnnotated(target)
        return when {
            isSkipped -> null
            annotated != null -> annotated.unstableApiUsageMessage(isOverride)
            isOverride || target.isDeclaredInFileOf(usage.element) -> null
            else -> signatureMatcher.find(target)?.unstableSignatureMessage(target.apiStatusName())
        }
    }
}

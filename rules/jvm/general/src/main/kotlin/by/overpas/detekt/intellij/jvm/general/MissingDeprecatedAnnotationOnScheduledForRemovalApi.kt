package by.overpas.detekt.intellij.jvm.general

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtDeclaration

@IntellijInspection("MissingDeprecatedAnnotationOnScheduledForRemovalApi")
class MissingDeprecatedAnnotationOnScheduledForRemovalApi(config: Config) :
    Rule(
        config,
        "A declaration is marked with `@ApiStatus.ScheduledForRemoval` but not with `@Deprecated`. " +
            "Add the `@Deprecated` annotation, so that the compiler warns the API users.",
    ),
    RequiresAnalysisApi {

    override fun visitDeclaration(dcl: KtDeclaration) {
        super.visitDeclaration(dcl)
        if (dcl.annotationEntries.isEmpty() || !dcl.isApiStatusDeclaration()) return
        val isReported = analyze(dcl) {
            dcl.findApiStatusEntry(API_STATUS_SCHEDULED_FOR_REMOVAL) != null &&
                dcl.findApiStatusEntry(JAVA_DEPRECATED, KOTLIN_DEPRECATED, SCALA_DEPRECATED) == null
        }
        if (isReported) report(Finding(Entity.from(dcl.apiStatusAnchor()), MESSAGE))
    }

    private companion object {
        const val MESSAGE = "Scheduled for removal API must also be marked with '@Deprecated' annotation"
    }
}

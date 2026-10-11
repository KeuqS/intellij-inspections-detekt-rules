package by.overpas.detekt.intellij.jvm.general.mustalreadyberemovedapi

import by.overpas.detekt.intellij.IntellijInspection
import by.overpas.detekt.intellij.jvm.general.API_STATUS_SCHEDULED_FOR_REMOVAL
import by.overpas.detekt.intellij.jvm.general.apiStatusAnchor
import by.overpas.detekt.intellij.jvm.general.findApiStatusEntry
import by.overpas.detekt.intellij.jvm.general.inVersion
import by.overpas.detekt.intellij.jvm.general.isApiStatusDeclaration
import dev.detekt.api.Config
import dev.detekt.api.Configuration
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import dev.detekt.api.config
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtDeclaration

@IntellijInspection("MustAlreadyBeRemovedApi")
class MustAlreadyBeRemovedApi(config: Config) :
    Rule(
        config,
        "A declaration is marked with `@ApiStatus.ScheduledForRemoval` for a version that is not later than " +
            "the current version. Remove the API.",
    ),
    RequiresAnalysisApi {

    @Configuration("The current version of the project. The rule reports nothing when it is empty.")
    private val currentVersion: String by config("")

    override fun visitDeclaration(dcl: KtDeclaration) {
        super.visitDeclaration(dcl)
        if (currentVersion.isEmpty() || dcl.annotationEntries.isEmpty() || !dcl.isApiStatusDeclaration()) return
        val removalVersion = analyze(dcl) { dcl.findApiStatusEntry(API_STATUS_SCHEDULED_FOR_REMOVAL)?.inVersion() }
        if (removalVersion == null || MustAlreadyBeRemovedApiVersions.compare(
                currentVersion,
                removalVersion,
            ) < 0
        ) {
            return
        }
        val message = if (currentVersion == removalVersion) {
            "API must be removed in the current version $currentVersion"
        } else {
            "API must have been removed in version $removalVersion but the current version is $currentVersion"
        }
        report(Finding(Entity.from(dcl.apiStatusAnchor()), message))
    }
}

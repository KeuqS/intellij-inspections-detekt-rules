package by.overpas.detekt.intellij.jvm.junit

import by.overpas.detekt.intellij.IntellijInspection
import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.RequiresAnalysisApi
import dev.detekt.api.Rule
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtClassOrObject

@IntellijInspection("Junit4RunWithInspection")
class Junit4RunWithInspection(config: Config) :
    Rule(
        config,
        "A test class has `@RunWith` while a parent class already has `@RunWith`. " +
            "JUnit 4 uses only one runner, so one of the annotations has no effect.",
    ),
    RequiresAnalysisApi {

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        super.visitClassOrObject(classOrObject)
        if (classOrObject.annotationEntries.isEmpty() || classOrObject.superTypeListEntries.isEmpty()) return
        analyze(classOrObject) {
            val runWith = classOrObject.annotationEntriesOf(ORG_JUNIT_RUN_WITH).firstOrNull() ?: return@analyze
            val parent = classOrObject.classSymbol?.parentWithRunWith() ?: return@analyze
            report(
                Finding(
                    Entity.from(runWith),
                    "'@RunWith' annotation already exists in ${parent.name?.asString()} class",
                ),
            )
        }
    }
}

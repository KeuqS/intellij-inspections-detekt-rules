package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtClass

private val JUNIT3_FORBIDDEN_ANNOTATIONS = listOf(
    ORG_JUNIT_TEST,
    ORG_JUNIT_IGNORE,
    ORG_JUNIT_BEFORE,
    ORG_JUNIT_AFTER,
    ORG_JUNIT_BEFORE_CLASS,
    ORG_JUNIT_AFTER_CLASS,
    JUPITER_TEST,
    JUPITER_DISABLED,
    JUPITER_BEFORE_EACH,
    JUPITER_AFTER_EACH,
    JUPITER_BEFORE_ALL,
    JUPITER_AFTER_ALL,
)

internal val JUNIT4_ANNOTATIONS = listOf(
    ORG_JUNIT_TEST,
    ORG_JUNIT_IGNORE,
    ORG_JUNIT_BEFORE,
    ORG_JUNIT_AFTER,
    ORG_JUNIT_BEFORE_CLASS,
    ORG_JUNIT_AFTER_CLASS,
)

internal val JUNIT5_ANNOTATIONS = listOf(
    JUPITER_TEST,
    JUPITER_PARAMETERIZED_TEST,
    JUPITER_TEST_FACTORY,
    JUPITER_REPEATED_TEST,
    JUPITER_DISABLED,
    JUPITER_BEFORE_EACH,
    JUPITER_AFTER_EACH,
    JUPITER_BEFORE_ALL,
    JUPITER_AFTER_ALL,
)

internal data class MixedJUnitAnnotation(
    val entry: KtAnnotationEntry,
    val message: String,
)

context(session: KaSession)
internal fun KtClass.mixedJUnitAnnotations(): List<MixedJUnitAnnotation> {
    val symbol = with(session) { classSymbol }
    val frameworks = listOf(JUNIT_FRAMEWORK_TEST_CASE, ORG_JUNIT_TEST, JUPITER_TEST).count { it.isOnClasspath() }
    val isMixedClass = frameworks > 1 && !hasRunWithInHierarchy()
    val version = symbol?.preferredJUnitVersion()?.takeIf { isMixedClass } ?: return emptyList()
    val forbidden = when (version) {
        JUNIT3 -> JUNIT3_FORBIDDEN_ANNOTATIONS
        JUNIT4 -> JUNIT5_ANNOTATIONS
        else -> JUNIT4_ANNOTATIONS
    }
    return functions().flatMap { function ->
        function.annotationEntriesIn(forbidden).map { entry ->
            val shortName = entry.resolvedClassId()?.run { shortClassName.asString() }.orEmpty()
            MixedJUnitAnnotation(
                entry,
                "Method '${function.name.orEmpty()}()' annotated with '@$shortName' inside class " +
                    "extending JUnit $version TestCase",
            )
        }
    }
}

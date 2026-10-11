package by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration

import by.overpas.detekt.intellij.jvm.junit.ORG_JUNIT_RUN_WITH
import by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration.MalformedClassIds.TEST_INSTANCE
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.annotations.KaAnnotationValue
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol

private val CHECKABLE_RUNNERS = setOf(
    "org.junit.runners.AllTests",
    "org.junit.runners.Parameterized",
    "org.junit.runners.BlockJUnit4ClassRunner",
    "org.junit.runners.JUnit4",
    "org.junit.runners.Suite",
    "org.junit.internal.runners.JUnit38ClassRunner",
    "org.junit.internal.runners.JUnit4ClassRunner",
    "org.junit.experimental.categories.Categories",
    "org.junit.experimental.runners.Enclosed",
)

context(session: KaSession)
internal fun KaClassSymbol.isTestInstancePerClass(): Boolean =
    selfAndSuperClasses().any { symbol ->
        symbol.annotations.any { annotation ->
            annotation.classId == TEST_INSTANCE &&
                annotation.arguments.any { argument ->
                    val entry = argument.expression as? KaAnnotationValue.EnumEntryValue
                    entry?.callableId?.run { callableName.asString() } == "PER_CLASS"
                }
        }
    }

context(session: KaSession)
internal fun KaClassSymbol.hasRunWith(): Boolean =
    selfAndSuperClasses().any { symbol -> symbol.annotations.any { it.classId == ORG_JUNIT_RUN_WITH } }

context(session: KaSession)
internal fun KaClassSymbol.hasCheckableRunner(): Boolean {
    val runWith = selfAndSuperClasses().firstNotNullOfOrNull { symbol ->
        symbol.annotations.firstOrNull { it.classId == ORG_JUNIT_RUN_WITH }
    }
    return runWith == null ||
        runWith.arguments.flatMap { it.expression.flatten() }
            .filterIsInstance<KaAnnotationValue.ClassLiteralValue>()
            .any { it.classId?.asFqNameString() in CHECKABLE_RUNNERS }
}

package by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration

import by.overpas.detekt.intellij.jvm.junit.JUPITER_TEST
import by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration.MalformedClassIds.REPETITION_INFO
import by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration.MalformedClassIds.TEST_INFO
import by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration.MalformedClassIds.TEST_REPORTER
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.psi.KtParameter

context(session: KaSession, settings: MalformedSettings)
internal fun KtParameter.isIgnorable(): Boolean =
    with(session) {
        symbol.hasMetaAnnotation { it.classId?.asFqNameString() in settings.ignorableAnnotations }
    }

context(session: KaSession)
internal fun ParameterRequirement.injects(type: KaType): Boolean {
    val requirement = this
    return with(session) {
        when (requirement) {
            ParameterRequirement.EACH_CALLBACK ->
                type.isClassType(TEST_INFO) || type.isClassType(REPETITION_INFO) || type.isClassType(TEST_REPORTER)

            ParameterRequirement.ALL_CALLBACK -> type.isClassType(TEST_INFO)

            ParameterRequirement.JUPITER_TEST -> type.isSubtypeOf(TEST_INFO) || type.isSubtypeOf(TEST_REPORTER)

            ParameterRequirement.NONE -> false
        }
    }
}

package by.overpas.detekt.intellij.jvm.junit

import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.REPETITION_INFO
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.TEST_INFO
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.TEST_REPORTER
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.psi.KtParameter

context(session: KaSession)
internal fun KtParameter.isIgnorable(settings: MalformedSettings): Boolean =
    with(session) {
        symbol.hasMetaAnnotation(mutableSetOf()) { it.classId?.asFqNameString() in settings.ignorableAnnotations }
    }

context(session: KaSession)
internal fun KaType.isInjectable(requirement: ParameterRequirement): Boolean =
    with(session) {
        when (requirement) {
            ParameterRequirement.EACH_CALLBACK ->
                isClassType(TEST_INFO) || isClassType(REPETITION_INFO) || isClassType(TEST_REPORTER)

            ParameterRequirement.ALL_CALLBACK -> isClassType(TEST_INFO)

            ParameterRequirement.JUPITER_TEST -> isSubtypeOf(TEST_INFO) || isSubtypeOf(TEST_REPORTER)

            ParameterRequirement.NONE -> false
        }
    }

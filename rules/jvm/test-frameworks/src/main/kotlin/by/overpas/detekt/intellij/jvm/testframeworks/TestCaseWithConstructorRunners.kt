package by.overpas.detekt.intellij.jvm.testframeworks

import org.jetbrains.kotlin.analysis.api.annotations.KaAnnotationValue
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.types.KaClassType

internal fun KaClassSymbol.isParameterized(): Boolean {
    val runner = findAnnotation(ORG_JUNIT_RUN_WITH)?.arguments?.firstOrNull()?.expression
    val runnerType = (runner as? KaAnnotationValue.ClassLiteralValue)?.type as? KaClassType
    return runnerType?.classId == ORG_JUNIT_PARAMETERIZED
}

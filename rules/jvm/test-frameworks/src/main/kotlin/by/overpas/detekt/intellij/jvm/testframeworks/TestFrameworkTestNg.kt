package by.overpas.detekt.intellij.jvm.testframeworks

import org.jetbrains.kotlin.analysis.api.annotations.KaAnnotationValue
import org.jetbrains.kotlin.analysis.api.base.KaConstantValue
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedFunctionSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbolOrigin
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbolVisibility

private const val TESTNG_ANNOTATIONS_PACKAGE = "org.testng.annotations"

internal fun KaNamedFunctionSymbol.isTestNgClassLevelCandidate(): Boolean =
    visibility == KaSymbolVisibility.PUBLIC &&
        origin != KaSymbolOrigin.SOURCE_MEMBER_GENERATED &&
        annotations.none { annotation ->
            annotation.classId?.run { packageFqName.asString() } == TESTNG_ANNOTATIONS_PACKAGE
        }

internal fun KaNamedFunctionSymbol.isDisabledTestNgTest(): Boolean {
    val enabled = findAnnotation(TESTNG_TEST)?.run { arguments.firstOrNull { it.name.asString() == "enabled" } }
    val constant = (enabled?.expression as? KaAnnotationValue.ConstantValue)?.value as? KaConstantValue.BooleanValue
    return constant?.value == false
}

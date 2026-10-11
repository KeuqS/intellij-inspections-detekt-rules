package by.overpas.detekt.intellij.jvm.junit.parameterizedparametersstaticcollection

import by.overpas.detekt.intellij.jvm.junit.ORG_JUNIT_PARAMETERIZED
import by.overpas.detekt.intellij.jvm.junit.ORG_JUNIT_PARAMETERS
import by.overpas.detekt.intellij.jvm.junit.ORG_JUNIT_RUN_WITH
import by.overpas.detekt.intellij.jvm.junit.annotationEntriesOf
import by.overpas.detekt.intellij.jvm.junit.functions
import by.overpas.detekt.intellij.jvm.junit.hasAnnotationEntry
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.types.KaClassType
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassLiteralExpression
import org.jetbrains.kotlin.psi.KtNamedFunction

internal data class ParameterizedParametersStaticCollectionProvider(
    val function: KtNamedFunction,
    val hasValidSignature: Boolean,
)

context(session: KaSession)
internal fun KtClass.parameterizedDataProviders(): List<ParameterizedParametersStaticCollectionProvider>? =
    if (isParameterizedTestClass()) {
        (functions() + companionObjects.flatMap { it.functions() })
            .filter { it.hasAnnotationEntry(ORG_JUNIT_PARAMETERS) }
            .map { ParameterizedParametersStaticCollectionProvider(it, it.hasDataProviderSignature()) }
    } else {
        null
    }

context(session: KaSession)
private fun KtClass.isParameterizedTestClass(): Boolean {
    val runner = annotationEntriesOf(ORG_JUNIT_RUN_WITH).firstOrNull()
        ?.valueArguments
        ?.firstOrNull()
        ?.getArgumentExpression() as? KtClassLiteralExpression ?: return false
    val kClassType = with(session) { runner.expressionType } as? KaClassType
    val runnerType = kClassType?.typeArguments?.firstOrNull()?.type
    return (with(session) { runnerType?.expandedSymbol })?.classId == ORG_JUNIT_PARAMETERIZED
}

package by.overpas.detekt.intellij.jvm.testframeworks.assertbetweeninconvertibletypes

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.resolution.KaFunctionCall
import org.jetbrains.kotlin.analysis.api.resolution.successfulFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.types.KaClassType
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.psi.KtCallExpression

context(session: KaSession)
internal fun KaType.isObjectType(): Boolean =
    (with(session) { lowerBoundIfFlexible() } as? KaClassType)?.classId == StandardClassIds.Any

context(session: KaSession)
internal fun KtCallExpression.resolveCall(): KaFunctionCall<*>? =
    with(session) { resolveToCall()?.successfulFunctionCallOrNull() }

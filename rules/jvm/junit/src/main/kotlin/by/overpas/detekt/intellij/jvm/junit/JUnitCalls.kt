package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.resolution.KaFunctionCall
import org.jetbrains.kotlin.analysis.api.resolution.successfulFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtCallExpression

context(session: KaSession)
internal fun KtCallExpression.resolvedFunctionCall(): KaFunctionCall<*>? =
    with(session) { resolveToCall()?.successfulFunctionCallOrNull() }

internal val KaFunctionCall<*>.ownerClassId: ClassId?
    get() = symbol.callableId?.classId

internal val KaFunctionCall<*>.functionName: String?
    get() = symbol.callableId?.callableName?.asString()

context(session: KaSession)
internal fun ClassId.isOnClasspath(): Boolean = with(session) { findClass(this@isOnClasspath) } != null

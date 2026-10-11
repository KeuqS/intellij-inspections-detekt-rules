package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.psi.KtExpression

context(session: KaSession)
internal fun KtExpression?.isThrowable(): Boolean {
    val type = this?.let { with(session) { it.expressionType } } ?: return false
    return with(session) { type.isSubtypeOf(StandardClassIds.Throwable) }
}

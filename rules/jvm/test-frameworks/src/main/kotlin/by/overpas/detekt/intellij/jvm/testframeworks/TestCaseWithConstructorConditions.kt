package by.overpas.detekt.intellij.jvm.testframeworks

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.base.KaConstantValue
import org.jetbrains.kotlin.psi.KtIfExpression

context(session: KaSession)
internal fun KtIfExpression.isTrivialIf(): Boolean {
    val constant = condition?.let { with(session) { it.evaluate() } } as? KaConstantValue.BooleanValue
    val branch = if (constant?.value == true) then else `else`
    return constant != null && branch?.isTrivialStatement() != false
}

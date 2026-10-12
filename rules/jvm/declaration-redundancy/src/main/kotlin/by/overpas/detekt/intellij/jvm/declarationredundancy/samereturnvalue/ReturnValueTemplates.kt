package by.overpas.detekt.intellij.jvm.declarationredundancy.samereturnvalue

import by.overpas.detekt.intellij.jvm.declarationredundancy.staticField
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression

context(session: KaSession)
internal fun KtExpression.returnValueTemplate(): String? =
    if (this is KtConstantExpression || this is KtStringTemplateExpression) {
        with(session) { evaluate() }?.run { value?.toString() ?: "null" }
    } else {
        staticField()?.callableId?.run { asSingleFqName().asString() }
    }

package by.overpas.detekt.intellij.jvm.general

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtAnnotationEntry

private const val IN_VERSION = "inVersion"

context(session: KaSession)
internal fun KtAnnotationEntry.inVersion(): String? {
    val argument = valueArguments.firstOrNull { it.getArgumentName()?.run { asName.asString() } == IN_VERSION }
    return with(session) { argument?.getArgumentExpression()?.evaluate()?.value as? String }
}

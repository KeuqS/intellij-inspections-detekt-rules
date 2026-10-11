package by.overpas.detekt.intellij.jvm.general.systemgetproperty

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.resolution.successfulFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtCallExpression

private val SYSTEM_GET_PROPERTY = CallableId(ClassId.fromString("java/lang/System"), Name.identifier("getProperty"))

private val SIMPLIFIABLE_PROPERTIES = setOf("file.separator", "path.separator", "line.separator", "file.encoding")

context(session: KaSession)
internal fun KtCallExpression.simplifiableSystemProperty(): String? =
    with(session) {
        val symbol = resolveToCall()?.successfulFunctionCallOrNull()?.symbol
        val isGetProperty = symbol?.callableId == SYSTEM_GET_PROPERTY && symbol.valueParameters.size == 1
        val value = valueArguments.single().getArgumentExpression()?.evaluate()?.value as? String
        value?.takeIf { isGetProperty && it in SIMPLIFIABLE_PROPERTIES }
    }

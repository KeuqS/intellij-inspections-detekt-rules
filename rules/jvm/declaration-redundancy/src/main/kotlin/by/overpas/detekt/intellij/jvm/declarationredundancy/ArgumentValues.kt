package by.overpas.detekt.intellij.jvm.declarationredundancy

import com.intellij.openapi.util.text.StringUtil
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.base.KaConstantValue
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtStringTemplateExpression

context(session: KaSession)
internal fun KtExpression.argumentValue(): ArgumentValue {
    val field = staticField()?.presentableName()
    val text = when {
        field != null -> field
        this is KtConstantExpression && node.elementType == KtNodeTypes.NULL -> "null"
        this is KtStringTemplateExpression && !hasInterpolation() -> "\"${entries.joinToString("") { it.text }}\""
        else -> with(session) { evaluate() }?.presentation()
    }
    return text?.let { ArgumentValue.Constant(it) } ?: ArgumentValue.NotConstant
}

private fun KaConstantValue.presentation(): String =
    when (this) {
        is KaConstantValue.LongValue -> "${value}L"

        is KaConstantValue.ShortValue -> "(short)$value"

        is KaConstantValue.ByteValue -> "(byte)$value"

        is KaConstantValue.StringValue -> "\"${StringUtil.escapeStringCharacters(value)}\""

        is KaConstantValue.CharValue -> "'${StringUtil.escapeCharCharacters(value.toString())}'"

        is KaConstantValue.IntValue -> value.toString()

        is KaConstantValue.DoubleValue -> value.toString()

        is KaConstantValue.FloatValue -> value.toString()

        is KaConstantValue.BooleanValue -> value.toString()

        is KaConstantValue.NullValue,
        is KaConstantValue.UByteValue,
        is KaConstantValue.UShortValue,
        is KaConstantValue.UIntValue,
        is KaConstantValue.ULongValue,
        is KaConstantValue.ErrorValue,
        -> render()
    }

package by.overpas.detekt.intellij.kotlin.styleissues.converttwocomparisonstorangecheck

import by.overpas.detekt.intellij.kotlin.styleissues.converttwocomparisonstorangecheck.ConvertTwoComparisonsToRangeCheckKind.FLOATING
import by.overpas.detekt.intellij.kotlin.styleissues.converttwocomparisonstorangecheck.ConvertTwoComparisonsToRangeCheckKind.SIGNED
import by.overpas.detekt.intellij.kotlin.styleissues.converttwocomparisonstorangecheck.ConvertTwoComparisonsToRangeCheckKind.UNSIGNED
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.psi.KtExpression

internal class ConvertTwoComparisonsToRangeCheckOperand(
    private val session: KaSession,
    private val expression: KtExpression,
    private val valueType: KaType,
) {

    private val type: KaType? = with(session) { expression.expressionType?.lowerBoundIfFlexible() }

    val kind: ConvertTwoComparisonsToRangeCheckKind? = type?.let { ConvertTwoComparisonsToRangeCheckKind.from(it) }

    val text: String?
        get() = with(session) {
            val valueKind = ConvertTwoComparisonsToRangeCheckKind.from(valueType)
            when {
                type == null -> null

                type.semanticallyEquals(valueType) -> expression.text

                valueKind == FLOATING && kind == SIGNED ->
                    (expression.evaluate()?.value as? Number)?.run { toDouble().toString() }

                valueKind == kind && kind in setOf(SIGNED, UNSIGNED, FLOATING) -> expression.text

                else -> null
            }
        }
}

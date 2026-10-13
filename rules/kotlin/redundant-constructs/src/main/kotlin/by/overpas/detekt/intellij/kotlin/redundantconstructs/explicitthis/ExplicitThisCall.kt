package by.overpas.detekt.intellij.kotlin.redundantconstructs.explicitthis

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.resolution.KaCallableMemberCall
import org.jetbrains.kotlin.analysis.api.resolution.KaExplicitReceiverValue
import org.jetbrains.kotlin.analysis.api.resolution.KaImplicitReceiverValue
import org.jetbrains.kotlin.analysis.api.resolution.KaReceiverValue
import org.jetbrains.kotlin.analysis.api.resolution.KaSmartCastedReceiverValue
import org.jetbrains.kotlin.analysis.api.resolution.successfulCallOrNull
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtThisExpression

internal class ExplicitThisCall(private val expression: KtExpression) {

    context(session: KaSession)
    fun target(): List<Any?>? =
        with(session) {
            expression.resolveToCall()?.successfulCallOrNull<KaCallableMemberCall<*, *>>()?.run {
                val symbol = partiallyAppliedSymbol.signature.symbol
                val receivers = listOf(
                    partiallyAppliedSymbol.dispatchReceiver,
                    partiallyAppliedSymbol.extensionReceiver,
                )
                listOf(symbol.psi, symbol.callableId) + receivers.map { targetPsi(it) }
            }
        }

    context(session: KaSession)
    private fun targetPsi(receiver: KaReceiverValue?): Any? =
        with(session) {
            when (receiver) {
                is KaImplicitReceiverValue -> receiver.symbol.psi

                is KaExplicitReceiverValue -> (receiver.expression as? KtThisExpression)
                    ?.instanceReference
                    ?.mainReference
                    ?.resolveToSymbol()
                    ?.psi

                is KaSmartCastedReceiverValue -> targetPsi(receiver.original)

                null -> null
            }
        }
}

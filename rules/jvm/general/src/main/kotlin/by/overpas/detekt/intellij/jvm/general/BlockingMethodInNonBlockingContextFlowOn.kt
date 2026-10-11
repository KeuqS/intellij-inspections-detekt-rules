package by.overpas.detekt.intellij.jvm.general

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.resolution.KaFunctionCall
import org.jetbrains.kotlin.analysis.api.resolution.successfulFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.psiUtil.parents

private val FLOW_PACKAGE = FqName("kotlinx.coroutines.flow")

private val FLOW_ON = FqName("kotlinx.coroutines.flow.flowOn")

internal class BlockingMethodInNonBlockingContextFlowOn(
    private val call: KaFunctionCall<*>,
    private val callExpression: KtExpression,
) {

    context(session: KaSession)
    fun dispatcher(): BlockingMethodInNonBlockingContextDispatcher =
        with(session) {
            val isFlow = call.symbol.callableId?.run { asSingleFqName().startsWith(FLOW_PACKAGE) } == true
            val flowOn = callExpression.parents
                .filterIsInstance<KtDotQualifiedExpression>()
                .firstNotNullOfOrNull { qualified ->
                    qualified.children.filterIsInstance<KtCallExpression>()
                        .mapNotNull { it.resolveToCall()?.successfulFunctionCallOrNull() }
                        .firstOrNull { call -> call.symbol.callableId?.run { asSingleFqName() } == FLOW_ON }
                }
            when {
                !isFlow -> BlockingMethodInNonBlockingContextDispatcher.NO_INFORMATION
                flowOn == null -> BlockingMethodInNonBlockingContextDispatcher.NON_BLOCKING
                else -> BlockingMethodInNonBlockingContextArgument(flowOn).dispatcher()
            }
        }
}

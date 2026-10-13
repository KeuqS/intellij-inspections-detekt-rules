package by.overpas.detekt.intellij.kotlin.coroutines.unusedflow

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.resolution.successfulFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.kotlin.psi.psiUtil.parents

private val MOCKK_RECORDING_FUNCTIONS = listOf(
    "verify",
    "coVerify",
    "verifyAll",
    "coVerifyAll",
    "verifyOrder",
    "coVerifyOrder",
    "verifySequence",
    "coVerifySequence",
    "excludeRecords",
).map { CallableId(FqName("io.mockk"), Name.identifier(it)) }

context(session: KaSession)
internal fun KtExpression.isInMockkRecording(): Boolean =
    with(session) {
        parents.filterIsInstance<KtLambdaExpression>()
            .mapNotNull { (it.parent as? KtValueArgument)?.getStrictParentOfType<KtCallExpression>() }
            .mapNotNull { it.resolveToCall()?.successfulFunctionCallOrNull()?.run { symbol.callableId } }
            .any { it in MOCKK_RECORDING_FUNCTIONS }
    }

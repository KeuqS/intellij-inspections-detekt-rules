package by.overpas.detekt.intellij.android.composepreview.previewshouldnotbecalledrecursively

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtCallExpression

context(session: KaSession)
internal fun KtCallExpression.calleeName(): Name? =
    with(session) { resolveToCall() }
        ?.singleFunctionCallOrNull()
        ?.run { symbol.callableId }
        ?.callableName

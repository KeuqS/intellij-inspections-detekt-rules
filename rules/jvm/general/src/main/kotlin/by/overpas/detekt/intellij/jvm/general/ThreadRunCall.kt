package by.overpas.detekt.intellij.jvm.general

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.resolution.successfulFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedFunctionSymbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtSuperExpression

private val THREAD_CLASS_ID = ClassId.fromString("java/lang/Thread")

internal fun KtCallExpression.isThreadRunOnSuper(): Boolean {
    val qualified = parent as? KtDotQualifiedExpression
    return qualified?.selectorExpression == this && qualified.receiverExpression is KtSuperExpression
}

context(session: KaSession)
internal fun KtCallExpression.isThreadRun(): Boolean =
    with(session) {
        val symbol = resolveToCall()?.successfulFunctionCallOrNull()?.symbol as? KaNamedFunctionSymbol
        val owner = symbol?.containingDeclaration as? KaClassSymbol
        val thread = findClass(THREAD_CLASS_ID)
        symbol != null &&
            !symbol.isStatic &&
            symbol.valueParameters.isEmpty() &&
            owner != null &&
            thread != null &&
            (owner.classId == THREAD_CLASS_ID || owner.isSubClassOf(thread))
    }

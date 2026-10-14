package by.overpas.detekt.intellij.kotlin.styleissues.implicitthis

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.components.KaImplicitReceiver
import org.jetbrains.kotlin.analysis.api.symbols.KaClassKind
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.idea.references.KtReference
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.psiUtil.parents

context(session: KaSession)
internal fun KtSimpleNameExpression.isImplicitThisAccess(): Boolean =
    with(session) {
        val reference = this@isImplicitThisAccess
        val symbol = reference.references.filterIsInstance<KtReference>().firstNotNullOfOrNull { it.resolveToSymbol() }
        val receivers = reference.containingKtFile.scopeContext(reference).implicitReceivers
        symbol?.implicitThisClass()?.canBeReferencedByThisIn(reference, receivers) == true
    }

context(session: KaSession)
private fun KaClassSymbol.canBeReferencedByThisIn(
    reference: KtSimpleNameExpression,
    implicitReceivers: List<KaImplicitReceiver>,
): Boolean {
    val receivers = implicitReceivers.map { it.toImplicitThisReceiver() }
    val index = receivers.indexOfFirst { it == null || isImplicitThisSuperOf(it) }
    val match = receivers.getOrNull(index) ?: return false
    if (classKind == KaClassKind.COMPANION_OBJECT && !reference.isInside(this)) return false
    val outerLabels = receivers.take(index).mapNotNull { it?.label }
    return match.label !in outerLabels && (index == 0 || match.label != null)
}

private fun KtSimpleNameExpression.isInside(classSymbol: KaClassSymbol): Boolean =
    parents
        .filterIsInstance<KtObjectDeclaration>()
        .firstOrNull { it.isCompanion() }
        ?.let { it == classSymbol.psi } == true

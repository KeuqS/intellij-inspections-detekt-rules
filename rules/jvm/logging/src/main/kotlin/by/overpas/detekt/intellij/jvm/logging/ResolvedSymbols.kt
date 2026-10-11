package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbol
import org.jetbrains.kotlin.analysis.api.types.KaClassType
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtNameReferenceExpression

context(session: KaSession)
internal fun KtExpression.resolvedSymbol(): KaSymbol? {
    val reference = selectorOrThis as? KtNameReferenceExpression ?: return null
    return with(session) { reference.mainReference.resolveToSymbol() }
}

context(session: KaSession)
internal val KaType.classIdOrNull: ClassId?
    get() = (with(session) { lowerBoundIfFlexible() } as? KaClassType)?.classId

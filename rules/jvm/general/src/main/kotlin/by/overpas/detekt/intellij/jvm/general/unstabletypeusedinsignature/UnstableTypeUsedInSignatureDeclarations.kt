package by.overpas.detekt.intellij.jvm.general.unstabletypeusedinsignature

import by.overpas.detekt.intellij.jvm.general.apiStatusClassId
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.psiUtil.parentsWithSelf

internal fun KtDeclaration.isUnstableTypeCandidate(): Boolean =
    !hasModifier(KtTokens.PRIVATE_KEYWORD) &&
        parentsWithSelf.filterIsInstance<KtClassOrObject>().none { owner ->
            (owner as? KtObjectDeclaration)?.isObjectLiteral() == true ||
                owner.isLocal ||
                owner.hasModifier(KtTokens.PRIVATE_KEYWORD)
        }

context(session: KaSession)
internal fun KtDeclaration.isInsideUnstableDeclaration(annotationNames: Set<String>): Boolean =
    parentsWithSelf.filterIsInstance<KtDeclaration>()
        .filter { it == this || it is KtClassOrObject }
        .any { declaration ->
            declaration.annotationEntries.any { it.apiStatusClassId()?.asFqNameString() in annotationNames }
        }

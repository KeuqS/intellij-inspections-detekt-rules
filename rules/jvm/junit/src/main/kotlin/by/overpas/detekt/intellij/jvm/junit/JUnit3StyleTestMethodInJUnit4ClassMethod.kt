package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.isPublic

private val NON_TEST_ANNOTATIONS = listOf(
    ORG_JUNIT_IGNORE,
    ORG_JUNIT_BEFORE,
    ORG_JUNIT_AFTER,
    JUPITER_BEFORE_EACH,
    JUPITER_AFTER_EACH,
)

internal fun KtNamedFunction.isJUnit3StyleTestCandidate(): Boolean =
    name.orEmpty().startsWith("test") &&
        containingClassOrObject is KtClass &&
        isPublic &&
        !hasModifier(KtTokens.ABSTRACT_KEYWORD) &&
        valueParameters.isEmpty() &&
        receiverTypeReference == null &&
        typeParameters.isEmpty()

context(session: KaSession)
internal fun KtNamedFunction.isJUnit3StyleTestInJUnit4Class(): Boolean {
    val containingClass = containingClassOrObject as? KtClass ?: return false
    val isUnitReturn = with(session) { symbol.returnType.isUnitType }
    return isUnitReturn &&
        !isJUnit4TestMethod() &&
        !isJUnit5TestMethod() &&
        annotationEntriesIn(NON_TEST_ANNOTATIONS).isEmpty() &&
        !containingClass.isJUnit3TestCase() &&
        containingClass.containsJUnit4Annotation()
}

context(session: KaSession)
private fun KtClass.containsJUnit4Annotation(): Boolean =
    (listOf<KtAnnotated>(this) + collectDescendantsOfType<KtAnnotated>())
        .flatMap { it.annotationEntries }
        .any { entry -> entry.resolvedClassId()?.run { packageFqName.asString().startsWith("org.junit") } == true }

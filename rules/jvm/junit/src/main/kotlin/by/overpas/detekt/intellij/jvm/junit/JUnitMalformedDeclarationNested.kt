package by.overpas.detekt.intellij.jvm.junit

import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.NESTED
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject

context(session: KaSession)
internal fun KtClass.isMalformedNested(): Boolean =
    with(session) {
        val enclosing = generateSequence(containingClassOrObject) { it.containingClassOrObject }.toList()
        val isRunnable = enclosing.isNotEmpty() && !enclosing.all { it.isAbstractOrInterface() }
        val outerAnnotations = enclosing.lastOrNull()?.classSymbol?.annotations
        val isJUnit4Malformed = isInner() &&
            hasDeclaredTests(ORG_JUNIT_TEST) &&
            outerAnnotations?.none { it.classId == ORG_JUNIT_RUN_WITH } == true
        val isNested = NESTED in annotationIds()
        val isJUnit5Malformed = if (isNested) {
            !isInner() || isPrivateMember()
        } else {
            isInner() && hasDeclaredTests(JUPITER_TEST, JUPITER_PARAMETERIZED_TEST)
        }
        isRunnable && (isJUnit4Malformed || isJUnit5Malformed)
    }

private fun KtClassOrObject.isAbstractOrInterface(): Boolean =
    this is KtClass && (isInterface() || hasModifier(KtTokens.ABSTRACT_KEYWORD))

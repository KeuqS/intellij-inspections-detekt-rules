package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject

private fun KtClassOrObject.ownerClass(): KtClassOrObject =
    if (this is KtObjectDeclaration && isCompanion()) containingClassOrObject ?: this else this

context(session: KaSession)
internal fun KtClassOrObject.isRunnable(): Boolean =
    with(session) { ownerClass().classSymbol?.hasCheckableRunner() != false }

context(session: KaSession)
internal fun KtClassOrObject.isPerClass(): Boolean =
    with(session) { classSymbol?.isTestInstancePerClass() == true }

context(session: KaSession)
internal fun KtClassOrObject.isJUnit3TestCase(): Boolean =
    with(session) {
        val owner = ownerClass().classSymbol
        owner != null && owner.defaultType.isSubtypeOf(JUNIT_FRAMEWORK_TEST_CASE) && !owner.hasRunWith()
    }

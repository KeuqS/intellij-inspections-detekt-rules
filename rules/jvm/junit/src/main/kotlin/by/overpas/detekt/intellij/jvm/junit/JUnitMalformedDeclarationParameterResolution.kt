package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject

context(session: KaSession)
internal fun KtNamedFunction.inParameterResolverContext(): Boolean =
    with(session) {
        symbol.hasParameterResolver() ||
            generateSequence(containingClassOrObject) { it.containingClassOrObject }.any { it.inParameterResolverContext() }
    }

context(session: KaSession)
private fun KtClassOrObject.inParameterResolverContext(): Boolean =
    with(session) {
        classSymbol?.run { selfAndSuperClasses().any { it.hasParameterResolver() } } == true || hasResolverExtensionField()
    }

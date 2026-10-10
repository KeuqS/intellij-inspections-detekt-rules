package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedFunction

context(session: KaSession)
internal fun KtClassOrObject.isJUnit5TestClass(): Boolean =
    functions().any { it.isJUnit5TestMethod() }

internal fun KtClassOrObject.functions(): List<KtNamedFunction> =
    declarations.filterIsInstance<KtNamedFunction>()

package by.overpas.detekt.intellij.jvm.testframeworks

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedFunctionSymbol
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction

context(session: KaSession)
internal fun KtNamedFunction.isTestWithoutAssertion(
    owner: KtClass,
    isThrowingIgnored: Boolean,
): Boolean {
    val function = with(session) { symbol } as? KaNamedFunctionSymbol
    val ownerSymbol = with(session) { owner.classSymbol }
    val isExceptionExpected = function?.findAnnotation(ORG_JUNIT_TEST)?.hasArgument("expected") == true ||
        function?.findAnnotation(TESTNG_TEST)?.hasArgument("expectedExceptions") == true
    val isThrowing = function?.hasAnnotation(KOTLIN_THROWS) == true ||
        function?.hasAnnotation(KOTLIN_JVM_THROWS) == true
    return function != null &&
        ownerSymbol != null &&
        function.isTestFunction(ownerSymbol) &&
        !isExceptionExpected &&
        !(isThrowingIgnored && isThrowing)
}

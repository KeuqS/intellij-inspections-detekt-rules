package by.overpas.detekt.intellij.jvm.testframeworks.testmethodwithoutassertion

import by.overpas.detekt.intellij.jvm.testframeworks.KOTLIN_JVM_THROWS
import by.overpas.detekt.intellij.jvm.testframeworks.KOTLIN_THROWS
import by.overpas.detekt.intellij.jvm.testframeworks.ORG_JUNIT_TEST
import by.overpas.detekt.intellij.jvm.testframeworks.TESTNG_TEST
import by.overpas.detekt.intellij.jvm.testframeworks.findAnnotation
import by.overpas.detekt.intellij.jvm.testframeworks.hasAnnotation
import by.overpas.detekt.intellij.jvm.testframeworks.hasArgument
import by.overpas.detekt.intellij.jvm.testframeworks.isTestFunction
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

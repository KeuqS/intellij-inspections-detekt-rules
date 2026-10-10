package by.overpas.detekt.intellij.jvm.testframeworks

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtAnonymousInitializer
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtSecondaryConstructor

context(session: KaSession)
internal fun KtClass.nonTrivialInitializers(): List<KtAnonymousInitializer> {
    val symbol = with(session) { classSymbol }
    val isTestClass = symbol?.isTestClass() == true
    return getAnonymousInitializers().filter { isTestClass && it.body?.isTrivialStatement() == false }
}

context(session: KaSession)
internal fun KtClass.nonTrivialConstructors(): List<KtSecondaryConstructor> {
    val symbol = with(session) { classSymbol }
    val isChecked = symbol != null &&
        symbol.isTestClass() &&
        !symbol.isParameterized() &&
        JUNIT_FRAMEWORK_TEST_SUITE !in symbol.superClassIds()
    return secondaryConstructors.filter { isChecked && it.bodyBlockExpression?.isTrivialStatement() == false }
}

package by.overpas.detekt.intellij.jvm.junit.junitmixedframework

import by.overpas.detekt.intellij.jvm.junit.functions
import by.overpas.detekt.intellij.jvm.junit.isJUnit3TestCase
import by.overpas.detekt.intellij.jvm.junit.superClassSymbols
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaFunctionSymbol

internal const val JUNIT3 = 3
internal const val JUNIT4 = 4
internal const val JUNIT5 = 5

context(session: KaSession)
internal fun KaClassSymbol.preferredJUnitVersion(): Int? =
    superClassSymbols().firstNotNullOfOrNull { it.ownJUnitVersion() } ?: ownJUnitVersion()

context(session: KaSession)
private fun KaClassSymbol.ownJUnitVersion(): Int? {
    val functions = with(session) { declaredMemberScope.callables.filterIsInstance<KaFunctionSymbol>().toList() }
    val junit4Methods = functions.count { function -> JUNIT4_ANNOTATIONS.any { it in function.annotations } }
    val junit5Methods = functions.count { function -> JUNIT5_ANNOTATIONS.any { it in function.annotations } }
    return when {
        isJUnit3TestCase() -> JUNIT3
        junit4Methods > junit5Methods -> JUNIT4
        junit5Methods > 0 -> JUNIT5
        else -> null
    }
}

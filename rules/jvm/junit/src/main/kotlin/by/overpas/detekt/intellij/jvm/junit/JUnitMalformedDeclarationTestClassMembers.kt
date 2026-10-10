package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedFunctionSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaSymbolVisibility
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtNamedFunction

internal const val JUNIT4_CONSTRUCTOR_REASON = "it should have exactly one 'public' no-arg constructor"

internal fun unconstructableMessage(name: String, reason: String): String =
    "Test class '$name' is not constructable because $reason"

context(session: KaSession)
internal fun KaNamedClassSymbol.hasJUnit4Tests(): Boolean =
    with(session) {
        !hasRunWith() &&
            memberScope.callables.any { callable ->
                callable is KaNamedFunctionSymbol && callable.annotations.any { annotation -> annotation.classId == ORG_JUNIT_TEST }
            }
    }

context(session: KaSession)
internal fun KaNamedClassSymbol.publicConstructorShapes(): List<List<KaType>> =
    with(session) {
        declaredMemberScope.constructors
            .filter { it.visibility == KaSymbolVisibility.PUBLIC || it.visibility == KaSymbolVisibility.INTERNAL }
            .flatMap { constructor ->
                val types = constructor.valueParameters.map { it.returnType }
                val hasDefaults = constructor.isPrimary && types.isNotEmpty() && constructor.valueParameters.all { it.hasDefaultValue }
                if (hasDefaults) listOf(types, emptyList()) else listOf(types)
            }
            .toList()
    }

context(session: KaSession)
internal fun KtClassOrObject.hasDeclaredTests(vararg annotations: ClassId): Boolean =
    with(session) {
        declarations.filterIsInstance<KtNamedFunction>().any { function ->
            function.symbol.annotations.any { it.classId in annotations }
        }
    }

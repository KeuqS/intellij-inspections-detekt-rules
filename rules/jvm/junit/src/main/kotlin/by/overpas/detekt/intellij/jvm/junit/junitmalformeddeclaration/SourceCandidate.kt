package by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration

import by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration.MalformedClassIds.JVM_STATIC
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedClassSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedFunctionSymbol
import org.jetbrains.kotlin.name.Name

internal data class SourceCandidate(
    val symbol: KaNamedFunctionSymbol,
    val isStatic: Boolean,
)

context(session: KaSession)
internal fun KaNamedClassSymbol.sourceCandidates(name: Name): List<SourceCandidate> =
    with(session) {
        val members = memberScope.callables { it == name }.filterIsInstance<KaNamedFunctionSymbol>()
            .map { SourceCandidate(it, it.isStatic) }
        val companionMembers = companionObject?.run { memberScope.callables { it == name } }.orEmpty()
            .filterIsInstance<KaNamedFunctionSymbol>()
            .map { function -> SourceCandidate(function, function.annotations.any { it.classId == JVM_STATIC }) }
        (members + companionMembers).toList()
    }

context(session: KaSession, provided: ProvidedParameters)
internal fun SourceCandidate.returnTypeMessage(sourceName: String): String? {
    val component = symbol.returnType.sourceComponentType()
    return when {
        component == null ->
            "Method source '$sourceName' must have one of the following return types: " +
                "'Stream<*>', 'Iterator<*>', 'Iterable<*>', 'Sequence<*>' or 'Array<*>'"

        provided.isMismatch && !component.isArgumentsLike() -> "Multiple parameters have to be wrapped in 'Arguments'"

        else -> null
    }
}

package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaVariableSymbol
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

private val SIMILAR_MESSAGE_METHODS = setOf(
    LogMethod.SLF4J,
    LogMethod.SLF4J_BUILDER,
    LogMethod.LOG4J,
    LogMethod.LOG4J_BUILDER,
    LogMethod.IDEA,
    LogMethod.AKKA,
)

internal class SimilarLogCalls(
    private val isErrorLogLevelSkipped: Boolean,
    minTextLength: Int,
) {

    private val similarPairs = SimilarPairs(MessageSimilarity(minTextLength))

    context(session: KaSession)
    fun find(file: KtFile): List<Pair<KtCallExpression, KtCallExpression>> {
        val hasLogger = listOf(SLF4J_LOGGER, LOG4J_LOGGER, IDEA_LOGGER).any { with(session) { findClass(it) } != null }
        if (!hasLogger) return emptyList()
        return file.collectDescendantsOfType<KtCallExpression>()
            .asSequence()
            .mapNotNull { call -> call.logMethod()?.takeIf { it in SIMILAR_MESSAGE_METHODS }?.let { call to it } }
            .filterNot { (call, method) -> isErrorLogLevelSkipped && SkippedLogCalls.isSkipped(call, method) }
            .groupBy { (call, _) -> (call.callReceiver?.resolvedSymbol() as? KaVariableSymbol)?.let { it.psi ?: it } }
            .values
            .asSequence()
            .flatMap { group -> group.groupBy { (call, _) -> call.calleeName }.values }
            .map { group ->
                group
                    .map { (call, method) ->
                        MessageLog(call, call.messageParts(method)?.flatMap { it.splitWithPlaceholders() })
                    }
                    .filter { log -> log.parts?.any(PartHolder::isText) == true }
            }
            .flatMap { group -> group.prefiltered().flatMap(similarPairs::find) }
            .toList()
    }
}

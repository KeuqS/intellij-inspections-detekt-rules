package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.name.ClassId

private const val TRACE = "trace"
private const val DEBUG = "debug"
private const val INFO = "info"
private const val WARN = "warn"
private const val ERROR = "error"
private const val FATAL = "fatal"
private const val LOG = "log"
private const val L7DLOG = "l7dlog"

internal enum class LogMethod(
    val classId: ClassId,
    val kind: LogMethodKind,
    val names: Set<String>,
) {
    SLF4J(SLF4J_LOGGER, LogMethodKind.LOGGER, setOf(TRACE, DEBUG, INFO, WARN, ERROR)),
    LOG4J(LOG4J_LOGGER, LogMethodKind.LOGGER, setOf(TRACE, DEBUG, INFO, WARN, ERROR, FATAL, LOG)),
    LOG4J_BUILDER(LOG4J_LOG_BUILDER, LogMethodKind.BUILDER, setOf(LOG)),
    SLF4J_BUILDER(SLF4J_EVENT_BUILDER, LogMethodKind.BUILDER, setOf(LOG)),
    LEGACY_LOG4J(LEGACY_LOG4J_LOGGER, LogMethodKind.LEGACY, setOf(TRACE, DEBUG, INFO, WARN, ERROR, FATAL, LOG, L7DLOG)),
    LEGACY_CATEGORY(LEGACY_CATEGORY_LOGGER, LogMethodKind.LEGACY, setOf(DEBUG, INFO, WARN, ERROR, FATAL, LOG, L7DLOG)),
    LEGACY_COMMONS(LEGACY_COMMONS_LOGGER, LogMethodKind.LEGACY, setOf(TRACE, DEBUG, INFO, WARN, ERROR, FATAL)),
    LEGACY_JAVA(
        LEGACY_JAVA_LOGGER,
        LogMethodKind.LEGACY,
        setOf("fine", LOG, "finer", "finest", "logp", "logrb", INFO, "severe", "warning", "config"),
    ),
    IDEA(IDEA_LOGGER, LogMethodKind.OTHER, setOf(TRACE, DEBUG, INFO, WARN, ERROR)),
    AKKA(AKKA_LOGGER, LogMethodKind.OTHER, setOf(DEBUG, ERROR, "format", INFO, LOG, "warning")),
}

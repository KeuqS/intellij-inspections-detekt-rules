package by.overpas.detekt.intellij.jvm.logging

import org.jetbrains.kotlin.name.ClassId

internal val SLF4J_LOGGER = ClassId.fromString("org/slf4j/Logger")
internal val LOG4J_LOGGER = ClassId.fromString("org/apache/logging/log4j/Logger")
internal val LOG4J_LOG_BUILDER = ClassId.fromString("org/apache/logging/log4j/LogBuilder")
internal val SLF4J_EVENT_BUILDER = ClassId.fromString("org/slf4j/spi/LoggingEventBuilder")
internal val LEGACY_LOG4J_LOGGER = ClassId.fromString("org/apache/log4j/Logger")
internal val LEGACY_CATEGORY_LOGGER = ClassId.fromString("org/apache/log4j/Category")
internal val LEGACY_COMMONS_LOGGER = ClassId.fromString("org/apache/commons/logging/Log")
internal val LEGACY_JAVA_LOGGER = ClassId.fromString("java/util/logging/Logger")
internal val AKKA_LOGGER = ClassId.fromString("akka/event/LoggingAdapter")
internal val IDEA_LOGGER = ClassId.fromString("com/intellij/openapi/diagnostic/Logger")
internal val LOG4J_LOG_MANAGER = ClassId.fromString("org/apache/logging/log4j/LogManager")
internal val SLF4J_TO_LOG4J_BRIDGE = ClassId.fromString("org/apache/logging/slf4j/Log4jLogger")
internal val STRUCTURED_ARGUMENT = ClassId.fromString("net/logstash/logback/argument/StructuredArgument")
internal val JAVA_SUPPLIER = ClassId.fromString("java/util/function/Supplier")
internal val LOG4J_SUPPLIER = ClassId.fromString("org/apache/logging/log4j/util/Supplier")

internal val LOGGER_CLASSES = setOf(
    SLF4J_LOGGER,
    LOG4J_LOGGER,
    LEGACY_LOG4J_LOGGER,
    LEGACY_CATEGORY_LOGGER,
    LEGACY_COMMONS_LOGGER,
    LEGACY_JAVA_LOGGER,
)

internal val BUILDER_CLASSES = setOf(SLF4J_EVENT_BUILDER, LOG4J_LOG_BUILDER)

internal val LEVEL_CLASSES = setOf(
    ClassId.fromString("org/apache/logging/log4j/Level"),
    ClassId.fromString("org/slf4j/event/Level"),
)

internal val LEGACY_LEVEL_CLASSES = setOf(
    ClassId.fromString("org/apache/logging/log4j/Level"),
    ClassId.fromString("org/apache/log4j/Priority"),
    ClassId.fromString("java/util/logging/Level"),
)

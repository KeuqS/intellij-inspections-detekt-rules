package by.overpas.detekt.intellij.jvm.logging

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class LoggingStringTemplateAsArgumentTest {

    private val environment = createEnvironment()

    private val sut = LoggingStringTemplateAsArgument(Config.empty)

    @Test
    fun `a debug call with a template of an object is reported`() {
        val code = $$"""
            import org.slf4j.LoggerFactory

            data class Data(val i: Int)

            class X {
                private val logger = LoggerFactory.getLogger(X::class.java)

                fun test() {
                    val data = Data(1)
                    logger.debug("data: $data")
                }
            }
        """.trimIndent()
        val expected = listOf("debug")

        val findings = sut.lintWithContext(environment, code)
        val reportedTexts = findings.map { finding ->
            finding.entity.location.text.run { code.substring(start, end) }
        }

        assertEquals(expected, reportedTexts)
    }

    @Test
    fun `calls above the debug level pass by default`() {
        val code = $$"""
            import org.apache.logging.log4j.LogManager

            data class Data(val i: Int)

            class X {
                private val logger = LogManager.getLogger()

                fun test() {
                    val data = Data(1)
                    logger.info("data: $data")
                    logger.warn("data: $data")
                    logger.atInfo().log("data: $data")
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `debug builder calls with templates are reported`() {
        val code = $$"""
            import org.apache.logging.log4j.LogManager
            import org.slf4j.LoggerFactory

            data class Data(val i: Int)

            class X {
                private val loggerSlf4J = LoggerFactory.getLogger(X::class.java)
                private val loggerLog4J = LogManager.getLogger()

                fun test() {
                    val data = Data(1)
                    loggerSlf4J.atDebug().log("data: $data")
                    loggerLog4J.atDebug().log("data: $data")
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(2, findings.size)
    }

    @Test
    fun `templates of primitives and strings pass by default`() {
        val code = $$"""
            import org.slf4j.LoggerFactory

            class X {
                private val logger = LoggerFactory.getLogger(X::class.java)

                fun test(text: String, number: Int?) {
                    logger.debug("variable: ${number}")
                    logger.debug("text: $text")
                    logger.debug("variable: {}", number)
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `templates of primitives are reported when primitives are not skipped`() {
        val code = $$"""
            import org.apache.logging.log4j.LogManager
            import org.slf4j.LoggerFactory

            class X {
                private val loggerSlf4J = LoggerFactory.getLogger(X::class.java)
                private val loggerLog4J = LogManager.getLogger()

                fun test() {
                    val variable1 = 1
                    loggerSlf4J.atInfo().log("variable1: ${variable1}")
                    loggerLog4J.info("variable1: ${variable1}")
                    loggerSlf4J.info("${variable1}")
                    loggerSlf4J.info("${getString()}")
                    loggerSlf4J.info("variable1: $variable1", RuntimeException())
                    loggerSlf4J.info("{} variable1: $variable1 {}", 1, 2)
                    loggerSlf4J.info("variable1: " + "$variable1")
                    loggerSlf4J.info("variable1: {}", variable1)
                }

                fun getString() = "test"
            }
        """.trimIndent()
        val sut = LoggingStringTemplateAsArgument(
            TestConfig("limitLevelType" to "ALL", "arePrimitivesSkipped" to false),
        )

        val findings = sut.lintWithContext(environment, code)

        assertEquals(7, findings.size)
    }

    @Test
    fun `templates that end with an exception pass`() {
        val code = $$"""
            import org.apache.logging.log4j.LogManager

            class X {
                private val logger = LogManager.getLogger()

                fun test() {
                    val exception = RuntimeException()
                    val variable1 = 1
                    logger.info("variable1: $variable1 exception: $exception")
                    logger.info("exception: $exception")
                    logger.info("variable1: $variable1", exception)
                }
            }
        """.trimIndent()
        val sut = LoggingStringTemplateAsArgument(
            TestConfig("limitLevelType" to "ALL", "arePrimitivesSkipped" to false),
        )

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a template with only an exception argument passes when such calls are skipped`() {
        val code = $$"""
            import org.apache.logging.log4j.LogManager

            class X {
                private val logger = LogManager.getLogger()

                fun test() {
                    val exception = RuntimeException()
                    val variable1 = 1
                    logger.info("variable1: $variable1", exception)
                }
            }
        """.trimIndent()
        val sut = LoggingStringTemplateAsArgument(
            TestConfig("limitLevelType" to "ALL", "arePrimitivesSkipped" to false, "isOnlyExceptionSkipped" to true),
        )

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `calls inside a matching level condition pass`() {
        val code = $$"""
            import org.slf4j.LoggerFactory

            data class Data(val i: Int)

            class X {
                private val logger = LoggerFactory.getLogger(X::class.java)

                fun test() {
                    val data = Data(1)
                    val atDebug = logger.atDebug()
                    if (logger.isDebugEnabled) {
                        logger.debug("$data")
                    }
                    if (logger.isDebugEnabled()) {
                        atDebug.log("$data")
                    }
                    if (logger.isTraceEnabled) {
                        atDebug.log("$data")
                    }
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a template passed to another class passes`() {
        val code = $$"""
            class Log {
                fun debug(message: String) = Unit
            }

            data class Data(val i: Int)

            fun test(log: Log, data: Data) {
                log.debug("$data")
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}

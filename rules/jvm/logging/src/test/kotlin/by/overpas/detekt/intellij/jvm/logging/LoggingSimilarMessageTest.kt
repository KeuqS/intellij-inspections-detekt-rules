package by.overpas.detekt.intellij.jvm.logging

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class LoggingSimilarMessageTest {

    private val environment = createEnvironment()

    private val sut = LoggingSimilarMessage(Config.empty)

    @Test
    fun `two calls with equal messages are reported`() {
        val code = """
            import org.apache.logging.log4j.LogManager
            import org.apache.logging.log4j.Logger

            class Logging {
                private val logger: Logger = LogManager.getLogger()

                private fun request1(i: String) {
                    val msg = "log messages: {}"
                    logger.info(msg, i)
                }

                private fun request2(i: Int) {
                    val msg = "log messages: {}"
                    logger.info(msg, i)
                }
            }
        """.trimIndent()
        val expected = listOf("info(msg, i)", "info(msg, i)")

        val findings = sut.lintWithContext(environment, code)
        val reportedTexts = findings.map { finding ->
            finding.entity.location.text.run { code.substring(start, end) }
        }

        assertEquals(expected, reportedTexts)
    }

    @Test
    fun `messages that start or end with the same text are reported`() {
        val code = $$"""
            import org.apache.logging.log4j.LogManager

            class Logging {
                private val logger = LogManager.getLogger()

                fun request1(i: String) {
                    logger.info("log messages" + i)
                    logger.warn(i + "2: log messages")
                }

                fun request2(i: Int) {
                    logger.info("log messages2" + i)
                    logger.warn("$i: log messages")
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(4, findings.size)
    }

    @Test
    fun `messages with the same middle text are reported`() {
        val code = $$"""
            import org.apache.logging.log4j.LogManager

            class Logging {
                private val logger = LogManager.getLogger()

                fun request1(i: String) {
                    logger.info(i + ": log messages: 2" + i)
                }

                fun request2(i: Int) {
                    logger.info("$i: log messages: $i")
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(2, findings.size)
    }

    @Test
    fun `messages that differ pass`() {
        val code = """
            import org.apache.logging.log4j.LogManager

            class Logging {
                private val logger = LogManager.getLogger()

                fun request1(i: String) {
                    logger.info("log 2 messages: " + i)
                    logger.warn(i + ": log 2 messages")
                    logger.debug("Hello World")
                    logger.trace(i + ": log messages: " + i)
                }

                fun request2(i: Int) {
                    logger.info("log messages: " + i)
                    logger.warn(i.toString() + ": log messages")
                    logger.debug("Hello World: test new")
                    logger.trace(i.toString() + ": log 2 messages: " + i)
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `messages of only placeholders and unknown parts pass`() {
        val code = """
            import org.apache.logging.log4j.LogManager

            class Logging {
                private val logger = LogManager.getLogger()

                fun request1(i: String) {
                    logger.info("{}" + i)
                    logger.warn("{} {}")
                }

                fun request2(i: Int) {
                    logger.info("{}" + i)
                    logger.warn("{} {}")
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a sequence of calls in one block passes`() {
        val code = """
            import org.apache.logging.log4j.LogManager

            class Logging {
                private val logger = LogManager.getLogger()

                fun request1() {
                    logger.info("testtesttest")
                    logger.info("testtesttest")
                    if (logger.isInfoEnabled()) {
                        logger.info("testtesttest")
                    }
                    if (logger.isInfoEnabled()) logger.info("testtesttest")
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `builder calls with similar messages are reported`() {
        val code = """
            import org.slf4j.LoggerFactory

            class X {
                private val logger = LoggerFactory.getLogger(X::class.java)

                fun foo() {
                    logger.atError().setMessage("aaaaa {}").log()
                }

                fun foo2() {
                    logger.atError().setMessage("aaaaa 2{}").log()
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(2, findings.size)
    }

    @Test
    fun `calls with an exception pass by default`() {
        val code = """
            import org.slf4j.LoggerFactory

            class X {
                private val logger = LoggerFactory.getLogger(X::class.java)

                fun foo(e: Exception) {
                    logger.error("request failed", e)
                }

                fun foo2(e: Exception) {
                    logger.error("request failed", e)
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `calls with an exception are reported when errors are not skipped`() {
        val code = """
            import org.slf4j.LoggerFactory

            class X {
                private val logger = LoggerFactory.getLogger(X::class.java)

                fun foo(e: Exception) {
                    logger.error("request failed", e)
                }

                fun foo2(e: Exception) {
                    logger.error("request failed", e)
                }
            }
        """.trimIndent()
        val sut = LoggingSimilarMessage(TestConfig("isErrorLogLevelSkipped" to false))

        val findings = sut.lintWithContext(environment, code)

        assertEquals(2, findings.size)
    }

    @Test
    fun `short similar texts pass with a longer minimum length`() {
        val code = """
            import org.slf4j.LoggerFactory

            class X {
                private val logger = LoggerFactory.getLogger(X::class.java)

                fun foo(i: Int) {
                    logger.info("value {}", i)
                }

                fun foo2(i: Int) {
                    logger.info("value {}", i)
                }
            }
        """.trimIndent()
        val sut = LoggingSimilarMessage(TestConfig("minTextLength" to 10))

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `many calls with equal messages are all reported`() {
        val code = """
            import org.apache.logging.log4j.LogManager

            class Logging {
                private val logger = LogManager.getLogger()

                fun request1(i: Int) {
                    logger.info("log messages: " + i)
                }

                fun request2(i: Int) {
                    logger.info("log messages: " + i)
                }

                fun request3(i: Int) {
                    logger.info("log messages: " + i)
                }

                fun request4(i: Int) {
                    logger.info("log messages: " + i)
                }

                fun request5(i: Int) {
                    logger.info("log messages: " + i)
                }

                fun request6(i: Int) {
                    logger.info("log messages: " + i)
                }

                fun request7(i: Int) {
                    logger.info("other text: " + i)
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(6, findings.size)
    }
}

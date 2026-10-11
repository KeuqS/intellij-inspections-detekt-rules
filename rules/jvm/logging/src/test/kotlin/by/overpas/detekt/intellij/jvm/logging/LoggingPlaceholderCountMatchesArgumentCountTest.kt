package by.overpas.detekt.intellij.jvm.logging

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class LoggingPlaceholderCountMatchesArgumentCountTest {

    private val environment = createEnvironment()

    private val sut = LoggingPlaceholderCountMatchesArgumentCount(Config.empty)

    @Test
    fun `slf4j calls with another number of arguments than placeholders are reported`() {
        val code = """
            import org.slf4j.Logger

            private val logger: Logger? = null
            private val brackets: String = "{}"

            fun foo() {
                logger?.debug("test {} {} {}", 1, 2)
                logger?.debug("test {} {}", *arrayOf(1, 2))
                logger?.debug("test {} {}", *arrayOf(1, 2, Exception()))
                logger?.debug("test " + brackets, 1, 2)
                logger?.debug("test {}" + brackets, 1, 2)
                logger?.debug("test {} {}", 1, 2)
                logger?.debug("test {} \\{} {}", 1)
                logger?.debug("test {} \\{} {}", 1, 2)
                logger?.debug("test {} {}", 1, Exception())
                logger?.debug("test {} {}", 1, 2, Exception())
                logger?.debug("test {}", 1, 2)
                logger?.error("test {} {} {}", 1, 2)
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(6, findings.size)
    }

    @Test
    fun `slf4j calls with partially known messages are reported`() {
        val code = $$"""
            import org.slf4j.LoggerFactory
            import java.util.Random

            class X {
                var logger = LoggerFactory.getLogger(X::class.java)

                fun m(t: String) {
                    logger.info("{} {}", 1, 2)
                    logger.info("{}" + t + 1 + "{}")
                    logger.info("{}" + t + 1)
                    logger.info("{}$t{}")
                    logger.info("{}$t{}", 1, 2)
                    val temp1 = "{} {}$t"
                    var temp = "{} {}$t"
                    logger.info(temp1, 1)
                    logger.info(temp, 1, 2, 3)
                    logger.info(logText, 1, 2, 3)
                    logger.info(logText, 1)
                    logger.info(logText3, 1)
                    temp = "{}$t"
                    logger.info(temp, 1)
                }

                fun m(i: Int, s: String) {
                    logger.info("test1 {}")
                    logger.info("test1 {}$s")
                    logger.info("test1 {}$i")
                }

                companion object {
                    private val logText = "{} {}" + something
                    private const val logText3 = "{} {}" + 1 + "{}"
                    private val something: String
                        get() = if (Random().nextBoolean()) "{}" else ""
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(9, findings.size)
    }

    @Test
    fun `slf4j builder calls with another number of arguments than placeholders are reported`() {
        val code = """
            import org.slf4j.LoggerFactory

            private val logger2 = LoggerFactory.getLogger("test")

            fun foo() {
                logger2.atError().log("{}", RuntimeException("test"))
                logger2.atError().log("{} {}", 1, RuntimeException("test"))
                logger2.atError().log("{}", 1, RuntimeException("test"))
                logger2.atError().log("{} {}", 1)
                val loggingEventBuilder = logger2.atError()
                loggingEventBuilder.log("{} {}", 2)
                logger2.atError().addArgument("s").addKeyValue("1", "1").log("{} {}", 2)
                logger2.atError().setMessage("{} {}").log()
                logger2.atError().addArgument("").addArgument("").setMessage("{} {}").log()
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(4, findings.size)
    }

    @Test
    fun `slf4j calls count an exception as a placeholder argument with log4j 2 as implementation`() {
        val code = """
            import org.slf4j.LoggerFactory

            fun foo() {
                val logger = LoggerFactory.getLogger("test")
                logger.info("string {} {}", 1, RuntimeException())
                logger.atError().log("{}", RuntimeException("test"))
                logger.atError().log("{} {}", 1, RuntimeException("test"))
                logger.atError().log("{}", 1, RuntimeException("test"))
            }
        """.trimIndent()
        val sut = LoggingPlaceholderCountMatchesArgumentCount(TestConfig("slf4jToLog4J2Type" to "YES"))

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `escaped slf4j placeholders are not counted`() {
        val code = """
            import org.slf4j.LoggerFactory

            class X {
                var log = LoggerFactory.getLogger(X::class.java)

                fun m() {
                    log.info("Created key {}\\\\{}", 1, 2)
                    log.info("Created key {}\\{}", 1, 2)
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `local variables and constants are evaluated`() {
        val code = """
            import org.slf4j.LoggerFactory

            private const val con = "{}"

            class X {
                var logger = LoggerFactory.getLogger(X::class.java)
                private var con2 = "{}"

                private fun slf4jVariable(number: String) {
                    logger.info(number + "1 {} {}", 1)
                    var t = "{}"
                    logger.info(con + t + "1", 1)
                    logger.info(con2 + t + "1", 1)
                }

                private fun slf4jVariable2() {
                    var t = "{}"
                    logger.info(con + t + "1", 1)
                    t = "1"
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(2, findings.size)
    }

    @Test
    fun `log4j2 calls with another number of arguments than placeholders are reported`() {
        val code = """
            import org.apache.logging.log4j.LogManager

            private val logger = LogManager.getLogger()
            private val brackets: String = "{}"

            fun foo() {
                logger?.debug("test {} {} {}", 1, 2)
                logger?.debug("test " + brackets, 1, 2)
                logger?.debug("test {}" + brackets, 1, 2)
                logger?.debug("test {} {} {}", 1, Exception())
                logger?.debug("test", 1, Exception())
                logger?.debug("test {}", Exception())
                logger?.debug("test {} {}", 1, 2, Exception())
                logger?.debug("test {} {}", 1, Exception())
                logger?.info("test {} {}", {}, {}, { RuntimeException() })
                logger?.info("test {} {}", {}, { RuntimeException() })
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(5, findings.size)
    }

    @Test
    fun `log4j2 builder calls with another number of arguments than placeholders are reported`() {
        val code = """
            import org.apache.logging.log4j.LogManager
            import org.apache.logging.log4j.util.Supplier

            private val logger = LogManager.getLogger()

            fun foo(i: Int) {
                logger?.atDebug()?.log("test {} {} {}", 1, 2)
                logger?.atDebug()?.log("test {} {}", 1, 2)
                logger?.atDebug()?.log("test", 1, Exception())
                logger?.atDebug()?.log("test {} {} {}", 1, 2, Exception())
                logger?.atDebug()?.log("test {}", Exception())
                logger.atError().log("test? ", Supplier<Any> { "" })
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(3, findings.size)
    }

    @Test
    fun `log4j2 suppliers of exceptions are not counted as arguments`() {
        val code = """
            import org.apache.logging.log4j.LogManager
            import org.apache.logging.log4j.util.Supplier

            class Logging {
                fun m() {
                    try {
                        throw RuntimeException()
                    } catch (t: Throwable) {
                        LOG.info("test {}", Supplier<Any> { "test" }, Supplier<Any> { t })
                        LOG.info("test {}", Supplier<Any> { "test" }, Supplier<Any> { "test" }, Supplier<Any> { t })
                        val s = Supplier { t }
                        LOG.info("test {}", Supplier<Any> { "test" }, s)
                        LOG.info("test {}", Supplier<Any> { "test" })
                        LOG.info("test {}", { "" }, { " " })
                        LOG.info("test {}", { "" }, { RuntimeException() })
                        val function: () -> String = { "RuntimeException()" }
                        LOG.info("test {}", { "" }, function)
                        val function2: () -> RuntimeException = { RuntimeException() }
                        LOG.info("test {}", { "" }, function2)
                    }
                }

                companion object {
                    private val LOG = LogManager.getLogger()
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(3, findings.size)
    }

    @Test
    fun `formatter logger calls are checked against format specifiers`() {
        val code = $$"""
            import org.apache.logging.log4j.LogManager
            import org.apache.logging.log4j.Logger

            private val LOG: Logger = LogManager.getFormatterLogger()

            fun m(t: String) {
                try {
                    throw RuntimeException()
                } catch (e: Throwable) {
                    val log2: Logger = LogManager.getFormatterLogger()
                    LOG.info("My %s text", "test", e)
                    LOG.info("My %i text", "test")
                    LOG.info("My %s text", "test1", "test2")
                    log2.info("My %s text, %s", "test1")
                    LogManager.getFormatterLogger().info("My %s text, %s", "test1")
                    LOG.info("%s" + t + 1 + "%s ")
                    LOG.info("%s %s" + t + 1)
                    LOG.info("%1$s %1$s", 1)
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(5, findings.size)
    }

    @Test
    fun `log4j2 loggers assigned in constructors are resolved`() {
        val code = """
            import org.apache.logging.log4j.LogManager
            import org.apache.logging.log4j.Logger

            class ConstructorInitializer {
                private val log: Logger

                constructor() {
                    log = LogManager.getLogger()
                }

                constructor(i: Int) {
                    log = LogManager.getLogger()
                }

                fun test() {
                    log.info("{}")
                }
            }

            class ConstructorInitializer2 {
                private val log: Logger

                constructor() {
                    log = LogManager.getFormatterLogger()
                }

                constructor(i: Int) {
                    log = LogManager.getLogger()
                }

                fun test() {
                    log.info("{}")
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `log4j2 text variables are evaluated`() {
        val code = """
            import org.apache.logging.log4j.LogManager

            class Logging {
                fun m(i: Int) {
                    val text = "test {}{}{}"
                    LOG.info(text, i)
                    val text2 = "test "
                    LOG.fatal(text2, i)
                    LOG.fatal(text + text, i)
                    LOG.info(FINAL_TEXT, i)
                    val sum = "first {}" + "second {}" + 1
                    LOG.info(sum, i)
                }

                companion object {
                    private const val FINAL_TEXT = "const"
                    private val LOG = LogManager.getLogger()
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(5, findings.size)
    }

    @Test
    fun `calls with arrays and of other classes pass`() {
        val code = """
            import org.slf4j.LoggerFactory

            class X {
                var log = LoggerFactory.getLogger(X::class.java)

                fun m(objects: Array<Any?>, a: String, notSlf4j: NotSlf4j) {
                    log.info("test {} test text {} text {}", *objects)
                    log.info("test {} for test {} in test {}", *arrayOf(a, 1, 2))
                    notSlf4j.info("not a trace message", "not a trace parameter")
                }
            }

            interface NotSlf4j {
                fun info(firstParameter: String?, secondParameter: Any?)
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}

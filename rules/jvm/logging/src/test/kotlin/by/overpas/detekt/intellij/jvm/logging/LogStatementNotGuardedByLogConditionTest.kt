package by.overpas.detekt.intellij.jvm.logging

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class LogStatementNotGuardedByLogConditionTest {

    private val environment = createEnvironment()

    private val sut = LogStatementNotGuardedByLogCondition(Config.empty)

    @Test
    fun `an unguarded slf4j debug call with a computed argument is reported`() {
        val code = """
            import org.slf4j.LoggerFactory

            class X {
                private val log = LoggerFactory.getLogger(X::class.java)

                fun n(arg: String) {
                    log.debug("test {}", arg)
                }
            }
        """.trimIndent()
        val expected = listOf("log.debug(\"test {}\", arg)")

        val findings = sut.lintWithContext(environment, code)
        val reportedTexts = findings.map { finding ->
            finding.entity.location.text.run { code.substring(start, end) }
        }

        assertEquals(expected, reportedTexts)
    }

    @Test
    fun `an unguarded log4j2 debug call with a computed argument is reported`() {
        val code = """
            import org.apache.logging.log4j.LogManager
            import org.apache.logging.log4j.Logger

            class X {
                fun n(arg: String) {
                    LOG.debug("test {}", arg)
                }

                companion object {
                    val LOG: Logger = LogManager.getLogger()
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a warn call passes with the default level limit`() {
        val code = """
            import org.slf4j.LoggerFactory

            class X {
                private val log = LoggerFactory.getLogger(X::class.java)

                fun n(arg: String) {
                    log.warn("test " + arg)
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `an info call is reported with the info level limit`() {
        val code = """
            import org.slf4j.LoggerFactory

            class X {
                private val log = LoggerFactory.getLogger(X::class.java)

                fun n(arg: String) {
                    log.info("test " + arg)
                }
            }
        """.trimIndent()
        val sut = LogStatementNotGuardedByLogCondition(TestConfig("limitLevelType" to "INFO_AND_LOWER"))

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `calls inside a matching level condition pass`() {
        val code = """
            import org.slf4j.LoggerFactory

            class X {
                private val log = LoggerFactory.getLogger(X::class.java)

                fun n(arg: String) {
                    if (log.isDebugEnabled) {
                        log.debug("test " + arg)
                    }
                    if (true && log.isDebugEnabled()) {
                        if (true) {
                            log.debug("test {}", arg)
                        }
                    }
                    if (!log.isDebugEnabled) {
                        return
                    } else {
                        log.debug("test {}", arg)
                    }
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a call inside a condition of another level is reported`() {
        val code = """
            import org.slf4j.LoggerFactory

            class X {
                private val log = LoggerFactory.getLogger(X::class.java)

                fun n(arg: String) {
                    if (log.isInfoEnabled) {
                        log.debug("test" + arg)
                    }
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `calls with only constant arguments pass`() {
        val code = """
            import org.slf4j.LoggerFactory

            class X {
                private val log = LoggerFactory.getLogger(X::class.java)

                fun n() {
                    log.debug("test")
                    log.debug("test {} {}", "test" + "test", 1 + 1)
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `calls with only constant arguments are reported when constants are flagged`() {
        val code = """
            import org.slf4j.LoggerFactory

            class X {
                private val log = LoggerFactory.getLogger(X::class.java)

                fun n1() {
                    log.debug("test")
                }

                fun n2() {
                    log.debug("test")
                }
            }
        """.trimIndent()
        val sut = LogStatementNotGuardedByLogCondition(TestConfig("isUnguardedConstantFlagged" to true))

        val findings = sut.lintWithContext(environment, code)

        assertEquals(2, findings.size)
    }

    @Test
    fun `only the first call of a sequence of similar calls is reported`() {
        val code = """
            import org.slf4j.LoggerFactory

            class X {
                private val log = LoggerFactory.getLogger(X::class.java)

                fun n3(arg: String) {
                    log.debug("test1" + arg)
                    log.debug("test2" + arg)
                    log.debug("test2" + arg)
                }

                fun constantCall(arg: String) {
                    log.debug("test1")
                    log.debug("test2" + arg)
                }

                fun differentLevels(arg: String) {
                    log.debug("test1" + arg)
                    log.warn("test2" + arg)
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(3, findings.size)
    }

    @Test
    fun `a call returned from a lambda passes`() {
        val code = """
            import org.slf4j.LoggerFactory

            class X {
                private val log = LoggerFactory.getLogger(X::class.java)

                fun n(arg: String) {
                    listOf(1).forEach { log.debug("test" + arg + it) }
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a debug call on another class passes`() {
        val code = """
            class Log {
                fun debug(message: String) = Unit
            }

            class X {
                private val log = Log()

                fun n(arg: String) {
                    log.debug("test" + arg)
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}

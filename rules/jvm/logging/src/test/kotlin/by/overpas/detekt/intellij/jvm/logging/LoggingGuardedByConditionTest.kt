package by.overpas.detekt.intellij.jvm.logging

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class LoggingGuardedByConditionTest {

    private val environment = createEnvironment()

    private val sut = LoggingGuardedByCondition(Config.empty)

    @Test
    fun `a slf4j call guarded by a level property is reported`() {
        val code = """
            import org.slf4j.LoggerFactory

            class X {
                private val log = LoggerFactory.getLogger(X::class.java)

                fun n(arg: String) {
                    if (log.isDebugEnabled) {
                        log.debug("test" + arg)
                    }
                }
            }
        """.trimIndent()
        val expected = listOf("isDebugEnabled")

        val findings = sut.lintWithContext(environment, code)
        val reportedTexts = findings.map { finding ->
            finding.entity.location.text.run { code.substring(start, end) }
        }

        assertEquals(expected, reportedTexts)
    }

    @Test
    fun `a log4j2 call guarded by a level method is reported`() {
        val code = """
            import org.apache.logging.log4j.LogManager
            import org.apache.logging.log4j.Logger

            class X {
                fun n(arg: String) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("test1" + arg)
                    }
                }

                companion object {
                    val LOG: Logger = LogManager.getLogger()
                }
            }
        """.trimIndent()
        val expected = listOf("isDebugEnabled()")

        val findings = sut.lintWithContext(environment, code)
        val reportedTexts = findings.map { finding ->
            finding.entity.location.text.run { code.substring(start, end) }
        }

        assertEquals(expected, reportedTexts)
    }

    @Test
    fun `a condition that guards several logging calls is reported`() {
        val code = """
            import org.slf4j.LoggerFactory

            class X {
                private val log = LoggerFactory.getLogger(X::class.java)

                fun n(arg: String) {
                    if (log.isDebugEnabled) {
                        log.info("test1" + arg)
                        log.debug("test2" + arg)
                    }
                    if (log.isDebugEnabled) log.debug("test3" + arg)
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(2, findings.size)
    }

    @Test
    fun `a condition that guards other code passes`() {
        val code = """
            import org.slf4j.LoggerFactory

            class X {
                private val log = LoggerFactory.getLogger(X::class.java)

                fun n(arg: String) {
                    if (log.isDebugEnabled) {
                        log.debug("test1" + arg)
                        println(arg)
                    }
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a condition that guards other code is reported when fixes are not required`() {
        val code = """
            import org.slf4j.LoggerFactory

            class X {
                private val log = LoggerFactory.getLogger(X::class.java)

                fun n(arg: String) {
                    if (log.isDebugEnabled) {
                        log.debug("test1" + arg)
                        println(arg)
                    }
                }
            }
        """.trimIndent()
        val sut = LoggingGuardedByCondition(TestConfig("isReportedOnlyIfFixPossible" to false))

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a condition of another level than the calls passes`() {
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

        assertEquals(0, findings.size)
    }

    @Test
    fun `a condition with another call passes`() {
        val code = """
            import org.slf4j.LoggerFactory

            class X {
                private val log = LoggerFactory.getLogger(X::class.java)

                fun n(arg: String) {
                    if (log.isDebugEnabled && arg.isNotEmpty()) {
                        log.debug("test" + arg)
                    }
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}

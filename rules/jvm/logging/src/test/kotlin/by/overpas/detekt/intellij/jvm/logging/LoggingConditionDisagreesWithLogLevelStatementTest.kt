package by.overpas.detekt.intellij.jvm.logging

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class LoggingConditionDisagreesWithLogLevelStatementTest {

    private val environment = createEnvironment()

    private val sut = LoggingConditionDisagreesWithLogLevelStatement(Config.empty)

    @Test
    fun `slf4j conditions of another level than the calls are reported`() {
        val code = """
            import org.slf4j.LoggerFactory

            class X {
                fun n() {
                    if (LOG.isInfoEnabled) {
                        LOG.info("nothing to report")
                    }
                    if (LOG.isInfoEnabled) {
                        LOG.warn("test")
                    }
                    if (LOG.isInfoEnabled()) {
                        LOG.warn("test")
                    }
                }

                companion object {
                    private val LOG = LoggerFactory.getLogger(X::class.java)
                }
            }
        """.trimIndent()
        val expected = listOf("isInfoEnabled", "isInfoEnabled")

        val findings = sut.lintWithContext(environment, code)
        val reportedTexts = findings.map { finding ->
            finding.entity.location.text.run { code.substring(start, end) }
        }

        assertEquals(expected, reportedTexts)
    }

    @Test
    fun `log4j2 conditions of another level than the calls are reported`() {
        val code = """
            import org.apache.logging.log4j.LogManager

            class Y {
                fun m() {
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("test")
                    }
                    if (LOG.isWarnEnabled) {
                        LOG.warn("test")
                    }
                    if (LOG.isInfoEnabled) {
                        LOG.warn("test")
                    }
                    if (LOG.isInfoEnabled()) {
                        LOG.atWarn().log("test")
                    }
                }

                companion object {
                    val LOG = LogManager.getLogger()
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(2, findings.size)
    }

    @Test
    fun `a condition that matches one of several calls passes`() {
        val code = """
            import org.slf4j.LoggerFactory

            object X {
                private val logger = LoggerFactory.getLogger(X::class.java)

                fun test() {
                    if (logger.isDebugEnabled) {
                        try {
                            something()
                            logger.debug("a")
                        } catch (e: Exception) {
                            logger.error("a")
                        }
                    }
                }

                private fun something() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a condition that matches none of several calls is reported for each call`() {
        val code = """
            import org.slf4j.LoggerFactory

            object X {
                private val logger = LoggerFactory.getLogger(X::class.java)

                fun test() {
                    if (logger.isInfoEnabled) {
                        try {
                            something()
                            logger.debug("a")
                        } catch (e: Exception) {
                            logger.error("a")
                        }
                    }
                }

                private fun something() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(2, findings.size)
    }

    @Test
    fun `a java util logging condition of another level is reported`() {
        val code = """
            import java.util.logging.Level
            import java.util.logging.Logger

            val LOG = Logger.getLogger("")

            class Loggers {
                fun method() {
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.warning("test")
                    }
                    if (LOG.isLoggable(Level.WARNING)) {
                        LOG.warning("not error")
                    }
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a log4j 1 condition of another level is reported`() {
        val code = """
            import org.apache.log4j.Logger

            class X {
                private val log = Logger.getLogger(X::class.java)

                fun method() {
                    if (log.isDebugEnabled) {
                        log.info("test")
                    }
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }
}

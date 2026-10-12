package by.overpas.detekt.intellij.jvm.general.systemgetproperty

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class SystemGetPropertyTest {

    private val environment = createEnvironment()

    private val sut = SystemGetProperty(Config.empty)

    @Test
    fun `calls with simplifiable properties are reported`() {
        val code = """
            fun foo() {
                System.getProperty("file.separator")
                System.getProperty("path.separator")
                System.getProperty("line.separator")
                System.getProperty("file.encoding")
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(4, findings.size)
    }

    @Test
    fun `a constant property name is reported`() {
        val code = """
            const val SEPARATOR = "line.separator"

            fun foo() = System.getProperty(SEPARATOR)
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `another property passes`() {
        val code = """
            fun foo() = System.getProperty("user.home")
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a call with a default value passes`() {
        val code = """
            fun foo() = System.getProperty("line.separator", "\n")
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `another getProperty function passes`() {
        val code = """
            object Settings {
                fun getProperty(name: String): String = name
            }

            fun foo() = Settings.getProperty("line.separator")
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}

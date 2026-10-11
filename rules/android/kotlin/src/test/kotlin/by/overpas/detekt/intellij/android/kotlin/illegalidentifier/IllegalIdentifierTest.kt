package by.overpas.detekt.intellij.android.kotlin.illegalidentifier

import dev.detekt.api.Config
import dev.detekt.test.lint
import dev.detekt.test.utils.compileContentForTest
import kotlin.test.Test
import kotlin.test.assertEquals

class IllegalIdentifierTest {

    private val sut = IllegalIdentifier(Config.empty)

    @Test
    fun `a backticked function name with a space is reported`() {
        val code = """
            fun `load the data`() {}
        """.trimIndent()
        val expected = listOf("`load the data`")

        val findings = sut.lint(code)
        val reportedTexts = findings.map { finding ->
            finding.entity.location.text.run { code.substring(start, end) }
        }

        assertEquals(expected, reportedTexts)
    }

    @Test
    fun `each backticked identifier with an illegal character is reported`() {
        val code = """
            fun `load the data`() {}

            fun call() {
                `load the data`()
            }
        """.trimIndent()

        val findings = sut.lint(code)

        assertEquals(2, findings.size)
    }

    @Test
    fun `a backticked property name with a dot is reported`() {
        val code = """
            val `user.name` = ""
        """.trimIndent()

        val findings = sut.lint(code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a backticked keyword passes`() {
        val code = """
            val `in` = 1
        """.trimIndent()

        val findings = sut.lint(code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a backticked name with a dash passes`() {
        val code = """
            val `user-name` = ""
        """.trimIndent()

        val findings = sut.lint(code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a backticked name with non latin letters passes`() {
        val code = """
            val `имя` = ""
        """.trimIndent()

        val findings = sut.lint(code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `an illegal character inside a string passes`() {
        val code = """
            val name = "`load the data`"
        """.trimIndent()

        val findings = sut.lint(code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a kotlin script passes`() {
        val code = """
            fun `load the data`() {}
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(0, findings.size)
    }
}

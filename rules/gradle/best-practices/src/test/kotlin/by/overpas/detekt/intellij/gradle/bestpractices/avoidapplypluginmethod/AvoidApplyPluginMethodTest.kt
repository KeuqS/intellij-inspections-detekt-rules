package by.overpas.detekt.intellij.gradle.bestpractices.avoidapplypluginmethod

import dev.detekt.api.Config
import dev.detekt.test.lint
import dev.detekt.test.utils.compileContentForTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AvoidApplyPluginMethodTest {

    private val sut = AvoidApplyPluginMethod(Config.empty)

    @Test
    fun `apply with a plugin id is reported`() {
        val code = """
            apply(plugin = "java")
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")
        val expected = listOf("""apply(plugin = "java")""")

        val findings = sut.lint(script)
        val reportedTexts = findings.map { finding ->
            finding.entity.location.text.run { code.substring(start, end) }
        }

        assertEquals(expected, reportedTexts)
    }

    @Test
    fun `apply with a plugin type is reported`() {
        val code = """
            apply<JavaPlugin>()
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(1, findings.size)
    }

    @Test
    fun `apply with a plugin id inside a block is reported`() {
        val code = """
            subprojects {
                apply(plugin = "java")
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(1, findings.size)
    }

    @Test
    fun `apply of a script file passes`() {
        val code = """
            apply(from = "other.gradle.kts")
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(0, findings.size)
    }

    @Test
    fun `the apply scope function passes`() {
        val code = """
            java.apply {
                withSourcesJar()
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(0, findings.size)
    }

    @Test
    fun `apply false in the plugins block passes`() {
        val code = """
            plugins {
                kotlin("jvm") version "2.0.0" apply false
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(0, findings.size)
    }

    @Test
    fun `apply with a plugin id in a kotlin file passes`() {
        val code = """
            fun Project.configure() {
                apply(plugin = "java")
            }
        """.trimIndent()

        val findings = sut.lint(code)

        assertEquals(0, findings.size)
    }
}

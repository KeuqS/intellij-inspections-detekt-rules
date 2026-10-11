package by.overpas.detekt.intellij.gradle.bestpractices.avoidrepositoriesinbuildgradle

import dev.detekt.api.Config
import dev.detekt.test.lint
import dev.detekt.test.utils.compileContentForTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AvoidRepositoriesInBuildGradleTest {

    private val sut = AvoidRepositoriesInBuildGradle(Config.empty)

    @Test
    fun `repositories in a build script are reported on the block name`() {
        val code = """
            repositories {
                mavenCentral()
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")
        val expected = listOf("repositories")

        val findings = sut.lint(script)
        val reportedTexts = findings.map { finding ->
            finding.entity.location.text.run { code.substring(start, end) }
        }

        assertEquals(expected, reportedTexts)
    }

    @Test
    fun `buildscript repositories are reported`() {
        val code = """
            buildscript {
                repositories {
                    gradlePluginPortal()
                }
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(1, findings.size)
    }

    @Test
    fun `publishing repositories pass`() {
        val code = """
            publishing {
                repositories {
                    maven { url = uri("https://repo.example.com/releases") }
                }
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(0, findings.size)
    }

    @Test
    fun `repositories in a settings script pass`() {
        val code = """
            dependencyResolutionManagement {
                repositories {
                    mavenCentral()
                }
            }
        """.trimIndent()
        val script = compileContentForTest(code, "settings.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(0, findings.size)
    }

    @Test
    fun `repositories in a precompiled script plugin pass`() {
        val code = """
            repositories {
                mavenCentral()
            }
        """.trimIndent()
        val script = compileContentForTest(code, "conventions.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(0, findings.size)
    }
}

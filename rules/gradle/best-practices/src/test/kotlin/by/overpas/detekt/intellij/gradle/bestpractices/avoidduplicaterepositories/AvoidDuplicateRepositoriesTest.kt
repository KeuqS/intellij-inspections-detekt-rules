package by.overpas.detekt.intellij.gradle.bestpractices.avoidduplicaterepositories

import dev.detekt.api.Config
import dev.detekt.test.lint
import dev.detekt.test.utils.compileContentForTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AvoidDuplicateRepositoriesTest {

    private val sut = AvoidDuplicateRepositories(Config.empty)

    @Test
    fun `each declaration of a duplicate repository is reported on its name`() {
        val code = """
            repositories {
                mavenCentral()
                mavenCentral()
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")
        val expected = listOf("mavenCentral", "mavenCentral")

        val findings = sut.lint(script)
        val reportedTexts = findings.map { finding ->
            finding.entity.location.text.run { code.substring(start, end) }
        }

        assertEquals(expected, reportedTexts)
    }

    @Test
    fun `a duplicate repository with other formatting is reported`() {
        val code = """
            repositories {
                maven { url = uri("https://repo1.maven.org/maven2/") }
                maven {
                    // Maven Central
                    url = uri("https://repo1.maven.org/maven2/")
                }
            }
        """.trimIndent()
        val script = compileContentForTest(code, "settings.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(2, findings.size)
    }

    @Test
    fun `a duplicate repository in a nested block is reported`() {
        val code = """
            pluginManagement {
                repositories {
                    gradlePluginPortal()
                    gradlePluginPortal()
                }
            }
        """.trimIndent()
        val script = compileContentForTest(code, "settings.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(2, findings.size)
    }

    @Test
    fun `different repositories pass`() {
        val code = """
            repositories {
                mavenCentral()
                maven { url = uri("https://repo1.maven.org/maven2/") }
                maven { url = uri("https://some.other.repo/") }
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(0, findings.size)
    }

    @Test
    fun `the same repository in two blocks passes`() {
        val code = """
            buildscript {
                repositories {
                    mavenCentral()
                }
            }

            repositories {
                mavenCentral()
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a duplicate repository in a kotlin file passes`() {
        val code = """
            fun Project.configure() {
                repositories {
                    mavenCentral()
                    mavenCentral()
                }
            }
        """.trimIndent()

        val findings = sut.lint(code)

        assertEquals(0, findings.size)
    }
}

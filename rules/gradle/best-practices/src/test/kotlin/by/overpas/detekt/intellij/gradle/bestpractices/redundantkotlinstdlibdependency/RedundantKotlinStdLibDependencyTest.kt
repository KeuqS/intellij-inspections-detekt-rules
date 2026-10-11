package by.overpas.detekt.intellij.gradle.bestpractices.redundantkotlinstdlibdependency

import dev.detekt.api.Config
import dev.detekt.test.lint
import dev.detekt.test.utils.compileContentForTest
import kotlin.test.Test
import kotlin.test.assertEquals

class RedundantKotlinStdLibDependencyTest {

    private val sut = RedundantKotlinStdLibDependency(Config.empty)

    @Test
    fun `a stdlib dependency with the plugin version is reported`() {
        val code = """
            plugins {
                kotlin("jvm") version "2.0.0"
            }

            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.0")
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")
        val expected = listOf("""implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.0")""")

        val findings = sut.lint(script)
        val reportedTexts = findings.map { finding ->
            finding.entity.location.text.run { code.substring(start, end) }
        }

        assertEquals(expected, reportedTexts)
    }

    @Test
    fun `a stdlib dependency in other notations with the plugin id is reported`() {
        val code = """
            plugins {
                id("org.jetbrains.kotlin.jvm") version "2.0.0"
            }

            dependencies {
                api(kotlin("stdlib", "2.0.0"))
                runtimeOnly(group = "org.jetbrains.kotlin", name = "kotlin-stdlib", version = "2.0.0")
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(2, findings.size)
    }

    @Test
    fun `a stdlib dependency with another version passes`() {
        val code = """
            plugins {
                kotlin("jvm") version "2.0.0"
            }

            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a compile only stdlib dependency passes`() {
        val code = """
            plugins {
                kotlin("jvm") version "2.0.0"
            }

            dependencies {
                compileOnly("org.jetbrains.kotlin:kotlin-stdlib:2.0.0")
                testCompileOnly("org.jetbrains.kotlin:kotlin-stdlib:2.0.0")
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a stdlib dependency with a configuration block passes`() {
        val code = """
            plugins {
                kotlin("jvm") version "2.0.0"
            }

            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.0") {
                    because("tests")
                }
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a stdlib dependency with a classifier passes`() {
        val code = """
            plugins {
                kotlin("jvm") version "2.0.0"
            }

            dependencies {
                implementation("org.jetbrains.kotlin", "kotlin-stdlib", "2.0.0", classifier = "sources")
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a stdlib dependency with a plugin that is not applied passes`() {
        val code = """
            plugins {
                kotlin("jvm") version "2.0.0" apply false
            }

            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.0")
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a stdlib dependency with another kotlin plugin passes`() {
        val code = """
            plugins {
                kotlin("multiplatform") version "2.0.0"
                id("java")
            }

            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.0")
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a stdlib dependency without a plugin version passes`() {
        val code = """
            plugins {
                kotlin("jvm")
            }

            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.0")
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(0, findings.size)
    }
}

package by.overpas.detekt.intellij.gradle.bestpractices.avoiddependencynamedargumentsnotation

import dev.detekt.api.Config
import dev.detekt.test.lint
import dev.detekt.test.utils.compileContentForTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AvoidDependencyNamedArgumentsNotationTest {

    private val sut = AvoidDependencyNamedArgumentsNotation(Config.empty)

    @Test
    fun `a dependency with named arguments is reported on its arguments`() {
        val code = """
            dependencies {
                implementation(group = "org.gradle", name = "gradle-core", version = "1.0")
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")
        val expected = listOf("""(group = "org.gradle", name = "gradle-core", version = "1.0")""")

        val findings = sut.lint(script)
        val reportedTexts = findings.map { finding ->
            finding.entity.location.text.run { code.substring(start, end) }
        }

        assertEquals(expected, reportedTexts)
    }

    @Test
    fun `a dependency with arguments in an unusual order is reported`() {
        val code = """
            dependencies {
                implementation(version = "1.0", name = "gradle-core", group = "org.gradle")
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a dependency with positional arguments is reported`() {
        val code = """
            dependencies {
                implementation("org.gradle", "gradle-core", "1.0")
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a dependency of a string configuration with named arguments is reported`() {
        val code = """
            dependencies {
                "customConf"(group = "org.gradle", name = "gradle-core") {
                    because("tests")
                }
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a dependency with a single string passes`() {
        val code = """
            dependencies {
                implementation("org.gradle:gradle-core:1.0")
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(0, findings.size)
    }

    @Test
    fun `an exclude with named arguments in a dependency block passes`() {
        val code = """
            dependencies {
                implementation("org.gradle:gradle-core:1.0") {
                    exclude(group = "com.google.guava", module = "guava")
                }
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a kotlin module with a version passes`() {
        val code = """
            dependencies {
                implementation(kotlin("stdlib", "2.0.0"))
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(0, findings.size)
    }

    @Test
    fun `an add call with the configuration name passes`() {
        val code = """
            dependencies {
                add("implementation", "org.gradle:gradle-core:1.0")
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a call with named arguments outside a dependencies block passes`() {
        val code = """
            configurations.all {
                exclude(group = "com.google.guava", module = "guava")
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a dependency with named arguments in a kotlin file passes`() {
        val code = """
            fun DependencyHandler.configure() {
                dependencies {
                    implementation(group = "org.gradle", name = "gradle-core", version = "1.0")
                }
            }
        """.trimIndent()

        val findings = sut.lint(code)

        assertEquals(0, findings.size)
    }
}

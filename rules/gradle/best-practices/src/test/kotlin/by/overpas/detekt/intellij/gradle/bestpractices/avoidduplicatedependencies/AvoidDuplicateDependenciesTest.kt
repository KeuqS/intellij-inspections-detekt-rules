package by.overpas.detekt.intellij.gradle.bestpractices.avoidduplicatedependencies

import dev.detekt.api.Config
import dev.detekt.test.lint
import dev.detekt.test.utils.compileContentForTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AvoidDuplicateDependenciesTest {

    private val sut = AvoidDuplicateDependencies(Config.empty)

    @Test
    fun `each declaration of a duplicate dependency is reported`() {
        val code = """
            dependencies {
                implementation("org.gradle:gradle-core:1.0")
                implementation("org.gradle:gradle-core:1.0")
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")
        val expected = listOf(
            """implementation("org.gradle:gradle-core:1.0")""",
            """implementation("org.gradle:gradle-core:1.0")""",
        )

        val findings = sut.lint(script)
        val reportedTexts = findings.map { finding ->
            finding.entity.location.text.run { code.substring(start, end) }
        }

        assertEquals(expected, reportedTexts)
    }

    @Test
    fun `a duplicate in another dependencies block is reported`() {
        val code = """
            dependencies {
                implementation("org.gradle:gradle-core:1.0")
            }

            dependencies {
                implementation("org.gradle:gradle-core:1.0")
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(2, findings.size)
    }

    @Test
    fun `a duplicate in other notations is reported`() {
        val code = """
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-reflect:2.0.0")
                implementation(kotlin("reflect", "2.0.0"))
                implementation(group = "org.jetbrains.kotlin", name = "kotlin-reflect", version = "2.0.0")
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(3, findings.size)
    }

    @Test
    fun `a duplicate version catalog dependency is reported`() {
        val code = """
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.coroutines.core) {
                    because("tests")
                }
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(2, findings.size)
    }

    @Test
    fun `the same dependency in two configurations passes`() {
        val code = """
            dependencies {
                implementation("org.gradle:gradle-core:1.0")
                testImplementation("org.gradle:gradle-core:1.0")
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(0, findings.size)
    }

    @Test
    fun `two versions of a dependency pass`() {
        val code = """
            dependencies {
                implementation("org.gradle:gradle-core:1.0")
                implementation("org.gradle:gradle-core:2.0")
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a duplicate in a nested dependencies block passes`() {
        val code = """
            subprojects {
                dependencies {
                    implementation("org.gradle:gradle-core:1.0")
                    implementation("org.gradle:gradle-core:1.0")
                }
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a duplicate in a kotlin file passes`() {
        val code = """
            fun DependencyHandler.configure() {
                add("implementation", "org.gradle:gradle-core:1.0")
                add("implementation", "org.gradle:gradle-core:1.0")
            }
        """.trimIndent()

        val findings = sut.lint(code)

        assertEquals(0, findings.size)
    }
}

package by.overpas.detekt.intellij.gradle.bestpractices.taskmissingdescription

import dev.detekt.api.Config
import dev.detekt.test.lint
import dev.detekt.test.utils.compileContentForTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TaskMissingDescriptionTest {

    private val sut = TaskMissingDescription(Config.empty)

    @Test
    fun `a registered task without a description is reported on the factory name`() {
        val code = """
            tasks.register("someTask") {}
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")
        val expected = listOf("register")

        val findings = sut.lint(script)
        val reportedTexts = findings.map { finding ->
            finding.entity.location.text.run { code.substring(start, end) }
        }

        assertEquals(expected, reportedTexts)
    }

    @Test
    fun `a typed task without a configuration block is reported`() {
        val code = """
            tasks.register<Copy>("someTask")
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a delegated task without a description is reported`() {
        val code = """
            val first by tasks.registering
            val second by tasks.creating(Copy::class) {}
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")
        val expected = listOf("registering", "creating")

        val findings = sut.lint(script)
        val reportedTexts = findings.map { finding ->
            finding.entity.location.text.run { code.substring(start, end) }
        }

        assertEquals(expected, reportedTexts)
    }

    @Test
    fun `a task without a description in a tasks block is reported`() {
        val code = """
            tasks {
                register("first")
                val second by registering {}
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(2, findings.size)
    }

    @Test
    fun `a task with a description passes`() {
        val code = """
            tasks.register("first") {
                description = "First task"
            }
            val second by tasks.registering {
                setDescription("Second task")
            }
            tasks {
                register<Copy>("third") {
                    if (true) {
                        description = "Third task"
                    }
                }
            }
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a configuration created by delegation passes`() {
        val code = """
            val customConf by configurations.creating
            val customSourceSet by sourceSets.creating {}
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a register call in another block passes`() {
        val code = """
            tasks {
                withType<Copy> {
                    register("nested")
                }
            }
            components.register("component")
        """.trimIndent()
        val script = compileContentForTest(code, "build.gradle.kts")

        val findings = sut.lint(script)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a registered task in a kotlin file passes`() {
        val code = """
            fun Project.configure() {
                tasks.register("someTask")
            }
        """.trimIndent()

        val findings = sut.lint(code)

        assertEquals(0, findings.size)
    }
}

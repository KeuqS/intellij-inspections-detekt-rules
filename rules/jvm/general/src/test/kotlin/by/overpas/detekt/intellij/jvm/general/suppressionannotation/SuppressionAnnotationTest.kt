package by.overpas.detekt.intellij.jvm.general.suppressionannotation

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class SuppressionAnnotationTest {

    private val environment = createEnvironment()

    private val sut = SuppressionAnnotation(Config.empty)

    @Test
    fun `suppress annotations are reported with their ids`() {
        val code = """
            @Suppress("PublicField", "HardCodedStringLiteral", "Other")
            class A {
                @Suppress("PublicField")
                var s: String? = null
                @Suppress
                var t: String? = null
                @Suppress(names = ["A", "B"])
                var u: String? = null
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(4, findings.size)
    }

    @Test
    fun `suppression comments are reported with their ids`() {
        val code = """
            fun foo() {
                //noinspection HardCodedStringLiteral
                any("hello")
                // noinspection
                any()
                //noinspection PublicField, HardCodedStringLiteral
                any()
            }

            private fun any(s: String? = null): String? = s
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(3, findings.size)
    }

    @Test
    fun `constant ids and the java annotation are reported`() {
        val code = """
            object Constants {
                const val PUBLIC_FIELD = "PublicField"
            }

            @SuppressWarnings(Constants.PUBLIC_FIELD)
            class A {
                @Suppress(*arrayOf("Unused"))
                var s: String = "test"
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(2, findings.size)
    }

    @Test
    fun `allowed suppressions pass`() {
        val code = """
            @Suppress("FreeSpeech")
            fun bar() {
                // noinspection FreeSpeech
                println()
            }
        """.trimIndent()
        val sut = SuppressionAnnotation(TestConfig("allowedSuppressions" to listOf("FreeSpeech")))

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `other annotations and comments pass`() {
        val code = """
            annotation class Suppress(val value: String)

            // a regular comment
            @Suppress("x")
            @Deprecated("old")
            fun bar() = Unit
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}

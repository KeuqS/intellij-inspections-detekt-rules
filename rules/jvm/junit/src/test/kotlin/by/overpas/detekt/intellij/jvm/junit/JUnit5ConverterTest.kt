package by.overpas.detekt.intellij.jvm.junit

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class JUnit5ConverterTest {

    private val environment = createEnvironment()

    private val sut = JUnit5Converter(Config.empty)

    @Test
    fun `a junit 4 test class is reported`() {
        val code = """
            import org.junit.Before
            import org.junit.Test

            class FooTest {
                @Before
                fun setUp() = Unit

                @Test
                fun foo() = Unit
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a junit 4 test class with a runner passes`() {
        val code = """
            import org.junit.Test
            import org.junit.runner.RunWith
            import org.junit.runners.JUnit4

            @RunWith(JUnit4::class)
            class FooTest {
                @Test
                fun foo() = Unit
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a junit 4 test class with a rule passes`() {
        val code = """
            import org.junit.Rule
            import org.junit.Test
            import org.junit.rules.TemporaryFolder

            class FooTest {
                @get:Rule
                val folder = TemporaryFolder()

                @Test
                fun foo() = Unit
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a junit 4 test class with a class rule in a companion object passes`() {
        val code = """
            import org.junit.ClassRule
            import org.junit.Test
            import org.junit.rules.TemporaryFolder

            class FooTest {
                @Test
                fun foo() = Unit

                companion object {
                    @JvmField
                    @ClassRule
                    val folder = TemporaryFolder()
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a junit 4 test with an expected exception passes`() {
        val code = """
            import org.junit.Test

            class FooTest {
                @Test(expected = IllegalStateException::class)
                fun foo() = Unit
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a junit 5 test class passes`() {
        val code = """
            import org.junit.jupiter.api.Test

            class FooTest {
                @Test
                fun foo() = Unit
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}

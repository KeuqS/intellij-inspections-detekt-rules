package by.overpas.detekt.intellij.jvm.junit

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class AssertEqualsCalledOnArrayTest {

    private val environment = createEnvironment()

    private val sut = AssertEqualsCalledOnArray(Config.empty)

    @Test
    fun `a junit 4 assertEquals on two arrays is reported`() {
        val code = """
            import org.junit.Assert

            class FooTest {
                fun test() {
                    Assert.assertEquals(arrayOf<Any>(), arrayOf<Any>())
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a junit 4 assertEquals with a message on two arrays is reported`() {
        val code = """
            import org.junit.Assert.assertEquals

            class FooTest {
                fun test(expected: Array<String>, actual: Array<String>) {
                    assertEquals("message", expected, actual)
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a junit 5 assertEquals on two arrays is reported`() {
        val code = """
            import org.junit.jupiter.api.Assertions

            class FooTest {
                fun test() {
                    Assertions.assertEquals(arrayOf<Any>(), arrayOf<Any>(), "message")
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `an assertEquals on two lists passes`() {
        val code = """
            import org.junit.jupiter.api.Assertions

            class FooTest {
                fun test() {
                    Assertions.assertEquals(listOf(1), listOf(1))
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a testng assertEquals on two arrays passes`() {
        val code = """
            import org.testng.Assert

            class FooTest {
                fun test() {
                    Assert.assertEquals(arrayOf<Any>(), arrayOf<Any>())
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `an own assertEquals on two arrays passes`() {
        val code = """
            fun assertEquals(expected: Any, actual: Any) = Unit

            fun test() {
                assertEquals(arrayOf<Any>(), arrayOf<Any>())
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}

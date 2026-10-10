package by.overpas.detekt.intellij.jvm.junit

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class AssertEqualsMayBeAssertSameTest {

    private val environment = createEnvironment()

    private val sut = AssertEqualsMayBeAssertSame(Config.empty)

    @Test
    fun `an assertEquals on final classes without equals is reported`() {
        val code = """
            import org.junit.Assert

            class Foo

            class FooTest {
                fun test() {
                    Assert.assertEquals(Foo(), Foo())
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a junit 5 assertEquals with a message on objects is reported`() {
        val code = """
            import org.junit.jupiter.api.Assertions.assertEquals

            object Foo

            class FooTest {
                fun test() {
                    assertEquals(Foo, Foo, "message")
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `an assertEquals on open classes passes`() {
        val code = """
            import org.junit.Assert

            open class Foo

            class FooTest {
                fun test() {
                    Assert.assertEquals(Foo(), Foo())
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `an assertEquals on classes that override equals passes`() {
        val code = """
            import org.junit.Assert

            data class Foo(val id: Int)

            class FooTest {
                fun test() {
                    Assert.assertEquals(Foo(1), Foo(1))
                    Assert.assertEquals("a", "a")
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `an assertEquals with one open class passes`() {
        val code = """
            import org.junit.Assert

            class Foo

            open class Bar

            class FooTest {
                fun test() {
                    Assert.assertEquals(Foo(), Bar())
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}

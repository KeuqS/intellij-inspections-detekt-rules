package by.overpas.detekt.intellij.jvm.junit

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class UseOfObsoleteAssertTest {

    private val environment = createEnvironment()

    private val sut = UseOfObsoleteAssert(Config.empty)

    @Test
    fun `a call of junit framework Assert is reported`() {
        val code = """
            import junit.framework.Assert

            class FooTest {
                fun test() {
                    Assert.assertTrue(true)
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
        assertEquals(
            "Call to 'assertTrue()' from 'junit.framework.Assert' should be replaced with call to method from 'org.junit.Assert'",
            findings.single().message,
        )
    }

    @Test
    fun `an inherited TestCase assertion is reported`() {
        val code = """
            import junit.framework.TestCase

            class FooTest : TestCase() {
                fun testFoo() {
                    assertEquals(1, 1)
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a call of org junit Assert passes`() {
        val code = """
            import org.junit.Assert

            class FooTest {
                fun test() {
                    Assert.assertTrue(true)
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `an instance method of TestCase passes`() {
        val code = """
            import junit.framework.TestCase

            class FooTest : TestCase() {
                fun testFoo() {
                    println(countTestCases())
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}

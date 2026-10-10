package by.overpas.detekt.intellij.jvm.junit

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class JUnit3StyleTestMethodInJUnit4ClassTest {

    private val environment = createEnvironment()

    private val sut = JUnit3StyleTestMethodInJUnit4Class(Config.empty)

    @Test
    fun `a test method without Test in a junit 4 class is reported`() {
        val code = """
            import org.junit.Test

            class FooTest {
                @Test
                fun foo() = Unit

                fun testBar() {
                    println()
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a test method in a class with a junit 4 runner is reported`() {
        val code = """
            import org.junit.runner.RunWith
            import org.junit.runners.JUnit4

            @RunWith(JUnit4::class)
            class FooTest {
                fun testBar() = Unit
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `an annotated test method passes`() {
        val code = """
            import org.junit.Test

            class FooTest {
                @Test
                fun testBar() = Unit
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `an ignored or setup method passes`() {
        val code = """
            import org.junit.Before
            import org.junit.Ignore
            import org.junit.Test

            class FooTest {
                @Ignore
                @Test
                fun testFoo() = Unit

                @Before
                fun testSetUp() = Unit
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a method that is not runnable passes`() {
        val code = """
            import org.junit.Test

            abstract class FooTest {
                @Test
                fun foo() = Unit

                private fun testPrivate() = Unit

                fun testWithParameter(value: Int) = Unit

                fun testWithResult(): Int = 1

                abstract fun testAbstract()
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a test method in a TestCase subclass passes`() {
        val code = """
            import junit.framework.TestCase
            import org.junit.Test

            class FooTest : TestCase() {
                @Test
                fun foo() = Unit

                fun testBar() = Unit
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a test method in a class without junit annotations passes`() {
        val code = """
            class FooTest {
                @Deprecated("old")
                fun testBar() = Unit
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}

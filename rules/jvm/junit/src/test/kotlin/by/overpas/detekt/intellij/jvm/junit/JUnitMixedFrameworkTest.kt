package by.overpas.detekt.intellij.jvm.junit

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class JUnitMixedFrameworkTest {

    private val environment = createEnvironment()

    private val sut = JUnitMixedFramework(Config.empty)

    @Test
    fun `a junit 5 annotation in a junit 4 class is reported`() {
        val code = """
            import org.junit.Before
            import org.junit.Test

            class FooTest {
                @Before
                fun setUp() = Unit

                @Test
                fun foo() = Unit

                @org.junit.jupiter.api.Test
                fun bar() = Unit
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a junit 4 annotation in a junit 5 class is reported`() {
        val code = """
            import org.junit.jupiter.api.Test

            class FooTest {
                @Test
                fun foo() = Unit

                @org.junit.Ignore
                @Test
                fun bar() = Unit
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `junit 4 and 5 annotations in a TestCase subclass are reported`() {
        val code = """
            import junit.framework.TestCase

            class FooTest : TestCase() {
                @org.junit.Test
                fun foo() = Unit

                @org.junit.jupiter.api.BeforeEach
                fun prepare() = Unit
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(2, findings.size)
    }

    @Test
    fun `junit 5 annotations in a subclass of a junit 4 class are reported`() {
        val code = """
            import org.junit.Test

            abstract class Base {
                @Test
                fun foo() = Unit
            }

            class FooTest : Base() {
                @org.junit.jupiter.api.Test
                fun bar() = Unit

                @org.junit.jupiter.api.Test
                fun baz() = Unit
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(2, findings.size)
    }

    @Test
    fun `a class with a runner passes`() {
        val code = """
            import org.junit.Test
            import org.junit.runner.RunWith
            import org.junit.runners.JUnit4

            @RunWith(JUnit4::class)
            class FooTest {
                @Test
                fun foo() = Unit

                @org.junit.jupiter.api.Test
                fun bar() = Unit
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a junit 5 class passes`() {
        val code = """
            import org.junit.jupiter.api.BeforeEach
            import org.junit.jupiter.api.Test

            class FooTest {
                @BeforeEach
                fun setUp() = Unit

                @Test
                fun foo() = Unit

                @Deprecated("old")
                fun bar() = Unit
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}

package by.overpas.detekt.intellij.jvm.junit

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class JUnit5AssertionsConverterTest {

    private val environment = createEnvironment()

    private val sut = JUnit5AssertionsConverter(Config.empty)

    @Test
    fun `a junit 4 assertion in a junit 5 test is reported`() {
        val code = """
            import org.junit.Assert
            import org.junit.jupiter.api.Test

            class FooTest {
                @Test
                fun test() {
                    Assert.assertEquals("message", 1, 1)
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
        assertEquals(
            "Call to 'assertEquals()' from 'Assert' should be replaced with a call to a method from " +
                "'org.junit.jupiter.api.Assertions'",
            findings.single().message,
        )
    }

    @Test
    fun `a junit 4 assumption in a parameterized junit 5 test is reported`() {
        val code = """
            import org.junit.Assume.assumeTrue
            import org.junit.jupiter.params.ParameterizedTest
            import org.junit.jupiter.params.provider.ValueSource

            class FooTest {
                @ParameterizedTest
                @ValueSource(ints = [1])
                fun test(value: Int) {
                    assumeTrue(value > 0)
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
        assertEquals(
            "Call to 'assumeTrue()' from 'Assume' should be replaced with a call to a method from " +
                "'org.junit.jupiter.api.Assumptions'",
            findings.single().message,
        )
    }

    @Test
    fun `a junit 4 assertThat in a junit 5 test is reported`() {
        val code = """
            import org.hamcrest.CoreMatchers.`is`
            import org.junit.Assert.assertThat
            import org.junit.jupiter.api.Test

            class FooTest {
                @Test
                fun test() {
                    assertThat(1, `is`(1))
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
        assertEquals(
            "Call to 'assertThat()' from 'Assert' should be replaced with a call to a method from " +
                "'org.hamcrest.MatcherAssert'",
            findings.single().message,
        )
    }

    @Test
    fun `a reference to a junit 4 assertion in a junit 5 test is reported`() {
        val code = """
            import org.junit.Assert
            import org.junit.jupiter.api.Test

            class FooTest {
                @Test
                fun test() {
                    listOf(true).forEach(Assert::assertTrue)
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a junit 4 assertion in a junit 4 test passes`() {
        val code = """
            import org.junit.Assert
            import org.junit.Test

            class FooTest {
                @Test
                fun test() {
                    Assert.assertEquals(1, 1)
                    listOf(true).forEach(Assert::assertTrue)
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a junit 5 assertion in a junit 5 test passes`() {
        val code = """
            import org.junit.jupiter.api.Assertions
            import org.junit.jupiter.api.Test

            class FooTest {
                @Test
                fun test() {
                    Assertions.assertEquals(1, 1)
                    listOf(true).forEach(Assertions::assertTrue)
                    listOf(1).forEach(::println)
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}

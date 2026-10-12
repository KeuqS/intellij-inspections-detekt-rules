package by.overpas.detekt.intellij.jvm.junit.migrateasserttomatcherassert

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class MigrateAssertToMatcherAssertTest {

    private val environment = createEnvironment()

    private val sut = MigrateAssertToMatcherAssert(Config.empty)

    @Test
    fun `a junit 4 assertEquals is reported`() {
        val code = """
            import org.junit.Assert

            class FooTest {
                fun test() {
                    Assert.assertEquals(1, 1)
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a junit 3 assertTrue with a comparison is reported`() {
        val code = """
            import junit.framework.Assert.assertTrue

            class FooTest {
                fun test(value: Int) {
                    assertTrue(value > 0)
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a junit 5 assertion passes`() {
        val code = """
            import org.junit.jupiter.api.Assertions

            class FooTest {
                fun test() {
                    Assertions.assertEquals(1, 1)
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a junit 4 assertThat passes`() {
        val code = """
            import org.hamcrest.CoreMatchers.`is`
            import org.junit.Assert

            class FooTest {
                fun test() {
                    Assert.assertThat(1, `is`(1))
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}

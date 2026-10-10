package by.overpas.detekt.intellij.jvm.junit

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class IgnoredJUnitTestTest {

    private val environment = createEnvironment()

    private val sut = IgnoredJUnitTest(Config.empty)

    @Test
    fun `a junit 4 test method ignored without reason is reported`() {
        val code = """
            import org.junit.Ignore
            import org.junit.Test

            class FooTest {
                @Ignore
                @Test
                fun test() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a junit 5 test class disabled without reason is reported`() {
        val code = """
            import org.junit.jupiter.api.Disabled
            import org.junit.jupiter.api.Test

            @Disabled
            class FooTest {
                @Test
                fun test() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a test disabled with a reason passes`() {
        val code = """
            import org.junit.jupiter.api.Disabled
            import org.junit.jupiter.api.Test

            class FooTest {
                @Disabled("flaky on CI")
                @Test
                fun test() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a test disabled with a reason is reported when all disabled tests are reported`() {
        val code = """
            import org.junit.Ignore
            import org.junit.Test

            class FooTest {
                @Ignore("flaky on CI")
                @Test
                fun test() {}
            }
        """.trimIndent()
        val rule = IgnoredJUnitTest(TestConfig("onlyReportWithoutReason" to false))

        val findings = rule.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `an annotation with the same name from another package passes`() {
        val code = """
            annotation class Ignore

            class FooTest {
                @Ignore
                fun test() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}

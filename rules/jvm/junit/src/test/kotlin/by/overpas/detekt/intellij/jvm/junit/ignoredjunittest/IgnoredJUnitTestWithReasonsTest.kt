package by.overpas.detekt.intellij.jvm.junit.ignoredjunittest

import dev.detekt.test.TestConfig
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class IgnoredJUnitTestWithReasonsTest {

    private val environment = createEnvironment()

    private val sut = IgnoredJUnitTest(TestConfig("isReasonAccepted" to false))

    @Test
    fun `a test ignored with a reason is reported`() {
        val code = """
            import org.junit.Ignore
            import org.junit.Test

            class FooTest {
                @Ignore("flaky on CI")
                @Test
                fun test() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }
}

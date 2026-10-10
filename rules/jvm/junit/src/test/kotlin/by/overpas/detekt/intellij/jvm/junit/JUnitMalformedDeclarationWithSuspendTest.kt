package by.overpas.detekt.intellij.jvm.junit

import dev.detekt.test.TestConfig
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class JUnitMalformedDeclarationWithSuspendTest {

    private val environment = createEnvironment()

    private val sut = JUnitMalformedDeclaration(TestConfig("areSuspendFunctionsAllowed" to true))

    @Test
    fun `a suspend junit 5 test method passes when suspend functions are allowed`() {
        val code = """
            import org.junit.jupiter.api.Test

            class FooTest {
                @Test
                suspend fun test() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}

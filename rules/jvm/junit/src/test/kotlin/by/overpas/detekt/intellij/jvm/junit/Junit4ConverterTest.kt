package by.overpas.detekt.intellij.jvm.junit

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class Junit4ConverterTest {

    private val environment = createEnvironment()

    private val sut = Junit4Converter(Config.empty)

    @Test
    fun `a TestCase subclass is reported`() {
        val code = """
            import junit.framework.TestCase

            class FooTest : TestCase() {
                fun testFoo() = Unit
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `an indirect TestCase subclass is reported`() {
        val code = """
            import junit.framework.TestCase

            abstract class BaseTest : TestCase()

            class FooTest : BaseTest()
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(2, findings.size)
    }

    @Test
    fun `a junit 4 test class passes`() {
        val code = """
            import org.junit.Test

            open class Base

            class FooTest : Base() {
                @Test
                fun foo() = Unit
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}

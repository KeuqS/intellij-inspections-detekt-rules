package by.overpas.detekt.intellij.jvm.junit

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class Junit4RunWithInspectionTest {

    private val environment = createEnvironment()

    private val sut = Junit4RunWithInspection(Config.empty)

    @Test
    fun `a RunWith on a subclass of a class with RunWith is reported`() {
        val code = """
            import org.junit.runner.RunWith
            import org.junit.runners.JUnit4
            import org.junit.runners.Suite

            @RunWith(Suite::class)
            abstract class Parent

            @RunWith(JUnit4::class)
            class Child : Parent()
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a RunWith on a grandparent class is reported`() {
        val code = """
            import org.junit.runner.RunWith
            import org.junit.runners.JUnit4

            @RunWith(JUnit4::class)
            abstract class GrandParent

            abstract class Parent : GrandParent()

            @RunWith(JUnit4::class)
            class Child : Parent()
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a RunWith only on the subclass passes`() {
        val code = """
            import org.junit.runner.RunWith
            import org.junit.runners.JUnit4

            abstract class Parent

            interface Marker

            @RunWith(JUnit4::class)
            class Child : Parent(), Marker
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a subclass without RunWith passes`() {
        val code = """
            import org.junit.runner.RunWith
            import org.junit.runners.JUnit4

            @RunWith(JUnit4::class)
            abstract class Parent

            @Deprecated("old")
            class Child : Parent()
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}

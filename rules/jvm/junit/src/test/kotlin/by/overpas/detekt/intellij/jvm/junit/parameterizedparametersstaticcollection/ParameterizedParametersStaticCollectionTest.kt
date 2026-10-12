package by.overpas.detekt.intellij.jvm.junit.parameterizedparametersstaticcollection

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class ParameterizedParametersStaticCollectionTest {

    private val environment = createEnvironment()

    private val sut = ParameterizedParametersStaticCollection(Config.empty)

    @Test
    fun `a parameterized class without a data provider is reported`() {
        val code = """
            import org.junit.runner.RunWith
            import org.junit.runners.Parameterized

            @RunWith(Parameterized::class)
            class FooTest(private val value: Int)
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a data provider that is not static is reported`() {
        val code = """
            import org.junit.runner.RunWith
            import org.junit.runners.Parameterized
            import org.junit.runners.Parameterized.Parameters

            @RunWith(Parameterized::class)
            class FooTest(private val value: Int) {
                @Parameters
                fun data(): List<Array<Any>> = listOf(arrayOf(1))
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a data provider with a wrong return type is reported`() {
        val code = """
            import org.junit.runner.RunWith
            import org.junit.runners.Parameterized
            import org.junit.runners.Parameterized.Parameters

            @RunWith(Parameterized::class)
            class FooTest(private val value: Int) {
                companion object {
                    @JvmStatic
                    @Parameters
                    fun data(): String = ""
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a private data provider is reported`() {
        val code = """
            import org.junit.runner.RunWith
            import org.junit.runners.Parameterized
            import org.junit.runners.Parameterized.Parameters

            @RunWith(Parameterized::class)
            class FooTest(private val value: Int) {
                companion object {
                    @JvmStatic
                    @Parameters
                    private fun data(): List<Int> = listOf(1)
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `multiple data providers are reported`() {
        val code = """
            import org.junit.runner.RunWith
            import org.junit.runners.Parameterized
            import org.junit.runners.Parameterized.Parameters

            @RunWith(Parameterized::class)
            class FooTest(private val value: Int) {
                companion object {
                    @JvmStatic
                    @Parameters
                    fun data(): List<Int> = listOf(1)

                    @JvmStatic
                    @Parameters
                    fun more(): List<Int> = listOf(2)
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a static iterable data provider passes`() {
        val code = """
            import org.junit.runner.RunWith
            import org.junit.runners.Parameterized
            import org.junit.runners.Parameterized.Parameters

            @RunWith(Parameterized::class)
            class FooTest(private val value: Int) {
                companion object {
                    @JvmStatic
                    @Parameters(name = "{0}")
                    fun data(): Collection<Array<Any>> = listOf(arrayOf(1))
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a static array data provider passes`() {
        val code = """
            import org.junit.runner.RunWith
            import org.junit.runners.Parameterized
            import org.junit.runners.Parameterized.Parameters

            @RunWith(Parameterized::class)
            class FooTest(private val value: Int) {
                companion object {
                    @JvmStatic
                    @Parameters
                    fun data(): Array<Array<Any>> = arrayOf(arrayOf(1))
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a class with another runner passes`() {
        val code = """
            import org.junit.runner.RunWith
            import org.junit.runners.JUnit4

            @RunWith(JUnit4::class)
            class FooTest
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}

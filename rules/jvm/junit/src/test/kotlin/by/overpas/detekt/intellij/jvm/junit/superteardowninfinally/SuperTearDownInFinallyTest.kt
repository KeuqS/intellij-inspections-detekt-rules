package by.overpas.detekt.intellij.jvm.junit.superteardowninfinally

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class SuperTearDownInFinallyTest {

    private val environment = createEnvironment()

    private val sut = SuperTearDownInFinally(Config.empty)

    @Test
    fun `a super tearDown call after other calls is reported`() {
        val code = """
            import junit.framework.TestCase

            class FooTest : TestCase() {
                override fun tearDown() {
                    println("clean up")
                    super.tearDown()
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a super tearDown call in a finally block passes`() {
        val code = """
            import junit.framework.TestCase

            class FooTest : TestCase() {
                override fun tearDown() {
                    try {
                        println("clean up")
                    } finally {
                        super.tearDown()
                    }
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a tearDown with only the super call passes`() {
        val code = """
            import junit.framework.TestCase

            class FooTest : TestCase() {
                var resource: String? = null

                override fun tearDown() {
                    resource = null
                    super.tearDown()
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a super tearDown call outside of a TestCase passes`() {
        val code = """
            open class Base {
                open fun tearDown() = Unit
            }

            class Foo : Base() {
                override fun tearDown() {
                    println("clean up")
                    super.tearDown()
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a super tearDown call from another method passes`() {
        val code = """
            import junit.framework.TestCase

            class FooTest : TestCase() {
                fun cleanUp() {
                    println("clean up")
                    super.tearDown()
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}

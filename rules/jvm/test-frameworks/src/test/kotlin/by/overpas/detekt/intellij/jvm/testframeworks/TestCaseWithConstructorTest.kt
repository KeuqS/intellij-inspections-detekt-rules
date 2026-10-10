package by.overpas.detekt.intellij.jvm.testframeworks

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class TestCaseWithConstructorTest {

    private val environment = createEnvironment()

    private val sut = TestCaseWithConstructor(Config.empty)

    @Test
    fun `a non-trivial secondary constructor in a junit 3 test case is reported`() {
        val code = """
            import junit.framework.TestCase

            class FooTest() : TestCase() {
                constructor(x: Int) : this() {
                    println(x)
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a non-trivial secondary constructor in a junit 4 test class is reported`() {
        val code = """
            import org.junit.Test

            class FooTest {
                constructor() {
                    println()
                }

                @Test
                fun testMe() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a non-trivial init block in a junit 5 test class is reported`() {
        val code = """
            import org.junit.jupiter.api.Test

            class FooTest {
                init {
                    println()
                }

                @Test
                fun test() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a non-trivial init block in a testng test class is reported`() {
        val code = """
            import org.testng.annotations.Test

            @Test
            class FooTest {
                init {
                    println()
                }

                fun test() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a constructor with a constant false condition passes`() {
        val code = """
            import junit.framework.TestCase

            class FooTest() : TestCase() {
                constructor(x: Int) : this() {
                    if (false) {
                        println(x)
                    }
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a constructor with a constant true condition is reported`() {
        val code = """
            import junit.framework.TestCase

            class FooTest() : TestCase() {
                constructor(x: Int) : this() {
                    if (true) {
                        println(x)
                    } else {
                        return
                    }
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a constructor with a non-constant condition is reported`() {
        val code = """
            import junit.framework.TestCase

            class FooTest() : TestCase() {
                constructor(x: Int) : this() {
                    if (x > 0) return
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a constructor that only throws or returns passes`() {
        val code = """
            import junit.framework.TestCase

            class FooTest() : TestCase() {
                constructor(x: Int) : this() {
                    throw IllegalStateException()
                }

                constructor(x: String) : this() {
                    return
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `an empty constructor and an empty init block pass`() {
        val code = """
            import junit.framework.TestCase

            class FooTest() : TestCase() {
                constructor(x: Int) : this()

                constructor(x: String) : this() {}

                init {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `an init block that assigns val properties passes`() {
        val code = """
            import org.junit.Test

            class FooTest {
                private val x: Int
                private val y: String

                init {
                    x = 1
                    this.y = "y"
                }

                @Test
                fun test() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `an init block that assigns a var property is reported`() {
        val code = """
            import org.junit.Test

            class FooTest {
                private var x: Int

                init {
                    x = compute()
                }

                @Test
                fun test() {}

                private fun compute() = 1
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a parameterized test class constructor passes`() {
        val code = """
            import org.junit.Test
            import org.junit.runner.RunWith
            import org.junit.runners.Parameterized

            @RunWith(Parameterized::class)
            class FooTest(private val x: Int) {
                constructor(x: String) : this(x.length) {
                    println(x)
                }

                @Test
                fun test() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a test suite constructor passes`() {
        val code = """
            import junit.framework.TestSuite

            class FooSuite : TestSuite {
                constructor() : super() {
                    addTestSuite(FooSuite::class.java)
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a constructor in a class that is not a test class passes`() {
        val code = """
            class Foo {
                constructor() {
                    println()
                }

                init {
                    println()
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}

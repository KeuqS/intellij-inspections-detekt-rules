package by.overpas.detekt.intellij.jvm.testframeworks.testcasewithnotestmethods

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class TestCaseWithNoTestMethodsTest {

    private val environment = createEnvironment()

    private val sut = TestCaseWithNoTestMethods(Config.empty)

    @Test
    fun `a junit 3 test case without test methods is reported`() {
        val code = """
            import junit.framework.TestCase

            class FooTest : TestCase() {
                override fun setUp() {
                    super.setUp()
                }

                fun testOne(): Int = 1

                private fun testThree() {}

                fun testFour(i: Int) {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a junit 3 test case with a test method passes`() {
        val code = """
            import junit.framework.TestCase

            class FooTest : TestCase() {
                fun testOne() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a junit 3 test case with a suite function passes`() {
        val code = """
            import junit.framework.Test
            import junit.framework.TestCase
            import junit.framework.TestSuite

            class FooTest : TestCase() {
                companion object {
                    @JvmStatic
                    fun suite(): Test = TestSuite()
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a junit 3 test case with a test only in a nested class is reported`() {
        val code = """
            import junit.framework.TestCase

            class FooTest : TestCase() {
                class Inner : TestCase() {
                    fun test1() {}
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `an ignored test class passes`() {
        val code = """
            import junit.framework.TestCase
            import org.junit.Ignore

            @Ignore
            class FooTest : TestCase()
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a test class with a runner passes`() {
        val code = """
            import junit.framework.TestCase
            import org.junit.runner.RunWith
            import org.junit.runners.Suite

            @RunWith(Suite::class)
            class FooTest : TestCase()
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a test class with a test in a superclass passes`() {
        val code = """
            import junit.framework.TestCase

            open class ParentTest : TestCase() {
                fun testInParent() {}
            }

            class FooTest : ParentTest()
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a test class with a test in a sealed junit 5 superclass passes`() {
        val code = """
            import org.junit.jupiter.api.Test

            sealed class ParentTest {
                @Test
                fun testInParent() {}
            }

            class FooTest : ParentTest() {
                @org.junit.jupiter.api.BeforeEach
                fun setUp() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a test class with a test in a superclass is reported when supers are not ignored`() {
        val code = """
            import junit.framework.TestCase

            open class ParentTest : TestCase() {
                fun testInParent() {}
            }

            class FooTest : ParentTest()
        """.trimIndent()
        val sut = TestCaseWithNoTestMethods(TestConfig("isSupersIgnored" to false))

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a test class with a runner on a superclass passes`() {
        val code = """
            import junit.framework.TestCase
            import org.junit.runner.RunWith
            import org.junit.runners.JUnit4

            @RunWith(JUnit4::class)
            abstract class ParentTest : TestCase()

            class FooTest : ParentTest()
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a test class with an unresolved superclass passes`() {
        val code = """
            import junit.framework.TestCase

            class FooTest : Unknown() {
                @org.junit.Before
                fun setUp() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a junit 5 test class with a nested class without tests is reported twice`() {
        val code = """
            import org.junit.jupiter.api.Nested

            class FooTest {
                @Nested
                inner class Inner {
                    private fun test1() {}
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(2, findings.size)
    }

    @Test
    fun `a junit 5 test class with a test in a nested class passes`() {
        val code = """
            import org.junit.jupiter.api.Nested
            import org.junit.jupiter.api.Test

            class FooTest {
                @Nested
                inner class Inner {
                    @Test
                    fun test1() {}
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a junit 4 test class with only life cycle methods is reported`() {
        val code = """
            import org.junit.Before

            class FooTest {
                @Before
                fun setUp() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a testng test class with a single disabled test is reported`() {
        val code = """
            import org.testng.annotations.Test

            class FooTest {
                @Test(enabled = false)
                fun foo() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a testng test class with an enabled test passes`() {
        val code = """
            import org.testng.annotations.Test

            class FooTest {
                @Test(enabled = false)
                fun foo() {}

                @Test
                fun bar() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a testng class level test with a public method passes`() {
        val code = """
            import org.testng.annotations.BeforeMethod
            import org.testng.annotations.Test

            @Test
            class FooTest {
                @BeforeMethod
                fun setUp() {}

                fun foo() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a testng class level test without public methods is reported`() {
        val code = """
            import org.testng.annotations.BeforeMethod
            import org.testng.annotations.Test

            @Test
            class FooTest {
                @BeforeMethod
                fun setUp() {}

                private fun foo() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a junit 5 test class with a meta annotated test passes`() {
        val code = """
            import org.junit.jupiter.api.Test

            @Test
            annotation class MyTest

            class FooTest {
                @MyTest
                fun foo() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a kotlin test class with a test passes`() {
        val code = """
            import kotlin.test.BeforeTest
            import kotlin.test.Test

            class FooTest {
                @BeforeTest
                fun setUp() {}

                @Test
                fun foo() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `abstract, open and plain classes pass`() {
        val code = """
            import junit.framework.TestCase

            abstract class AbstractTest : TestCase()

            open class OpenTest : TestCase()

            class Foo

            interface Bar
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}

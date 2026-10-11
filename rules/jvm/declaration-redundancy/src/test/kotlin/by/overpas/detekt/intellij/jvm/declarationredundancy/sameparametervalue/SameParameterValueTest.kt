package by.overpas.detekt.intellij.jvm.declarationredundancy.sameparametervalue

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class SameParameterValueTest {

    private val environment = createEnvironment()

    private val sut = SameParameterValue(Config.empty)

    @Test
    fun `a private function always called with the same value is reported`() {
        val code = """
            class Test {
                private fun foo(value: Int) = value

                fun bar() {
                    foo(10)
                    foo(10)
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a private function called with different values passes`() {
        val code = """
            class Test {
                private fun foo(value: Int) = value

                fun bar() {
                    foo(10)
                    foo(20)
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a public function passes`() {
        val code = """
            class Test {
                fun foo(value: Int) = value

                fun bar() = foo(10)
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a public function of a private class passes by default`() {
        val code = """
            private class Test {
                fun foo(value: Int) = value

                fun bar() = foo(10)
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a public function of a private class is reported when public functions are reported`() {
        val code = """
            private class Test {
                fun foo(value: Int) = value

                fun bar() = foo(10)
            }
        """.trimIndent()
        val sut = SameParameterValue(TestConfig("highestModifier" to "public"))

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a protected function passes when only private functions are reported`() {
        val code = """
            private class Test {
                protected fun foo(value: Int) = value

                fun bar() = foo(10)
            }
        """.trimIndent()
        val sut = SameParameterValue(TestConfig("highestModifier" to "private"))

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `named arguments with the same values are reported`() {
        val code = """
            class TheSame {
                private fun foo(arg1: String, arg2: String) {}

                fun bar() {
                    foo("foo", "bar")
                    foo(arg1 = "foo", arg2 = "bar")
                    foo(arg2 = "bar", arg1 = "foo")
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(2, findings.size)
    }

    @Test
    fun `named arguments with swapped values pass`() {
        val code = """
            class TestDifferent {
                private fun foo(arg1: String, arg2: String) {}

                fun bar() {
                    foo("foo", "bar")
                    foo(arg2 = "foo", arg1 = "bar")
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `the same varargs are reported`() {
        val code = """
            class TheSameVarargIsLast {
                private fun foo(args1: String, vararg args: String) {}

                fun bar() {
                    foo("foo", "bar", "foobar")
                    foo("bar", "bar", "foobar")
                    foo(args1 = "bar", "bar", "foobar")
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `different varargs pass`() {
        val code = """
            class TestDifferent {
                private fun foo(vararg args: String) {}

                fun bar() {
                    foo("foo", "bar")
                    foo("foobar")
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a spread argument passes`() {
        val code = """
            class Test {
                private fun foo(vararg args: String) {}

                fun bar(values: Array<String>) {
                    foo(*values)
                    foo(*values)
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `the same varargs of an extension function are reported`() {
        val code = """
            fun String.publicFunction() = privateFunctionWithReceiverAndVarargs(1, 2)
            fun String.publicFunction2() = privateFunctionWithReceiverAndVarargs(1, 2)

            private fun String.privateFunctionWithReceiverAndVarargs(vararg fields: Int) {}
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a call with a default argument passes`() {
        val code = """
            interface Fixture

            private fun Fixture.resolve(projectPath: String = "F") {}

            fun main(f: Fixture) {
                f.resolve("F")
                f.resolve("F")
                f.resolve()
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a function reference passes`() {
        val code = """
            private fun foo(value: Int) = value

            fun bar(): Int {
                val reference = ::foo
                return foo(1) + reference(2)
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a negative double is reported`() {
        val code = """
            private object Test {
                private fun someMethod(xxx: Double) = xxx

                fun bar() = someMethod(-1.111)
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a constant reference is reported`() {
        val code = """
            const val LIMIT = 10

            private fun foo(value: Int) = value

            fun bar() = foo(LIMIT) + foo(LIMIT)
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a null argument is reported`() {
        val code = """
            private fun foo(value: String?) = value

            fun bar() = foo(null)
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a function with fewer usages than the minimal count passes`() {
        val code = """
            private fun foo(value: Int) = value

            fun bar() = foo(1)
        """.trimIndent()
        val sut = SameParameterValue(TestConfig("minimalUsageCount" to 2))

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `an unused function passes`() {
        val code = """
            private fun foo(value: Int) = value
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `an override in a private class passes`() {
        val code = """
            interface Base {
                fun foo(value: Int): Int
            }

            private class Impl : Base {
                override fun foo(value: Int) = value

                fun bar() = foo(1)
            }
        """.trimIndent()
        val sut = SameParameterValue(TestConfig("highestModifier" to "public"))

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a variable argument passes`() {
        val code = """
            private fun foo(value: Int) = value

            fun bar(value: Int) = foo(value)
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}

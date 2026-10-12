package by.overpas.detekt.intellij.jvm.declarationredundancy.samereturnvalue

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class SameReturnValueTest {

    private val environment = createEnvironment()

    private val sut = SameReturnValue(Config.empty)

    @Test
    fun `a function that returns the same value from nested branches is reported`() {
        val code = """
            fun test(num: Int, flag1: Boolean, flag2: Boolean): Int {
                if (flag1) {
                    return (42)
                }
                return if (flag2) {
                    (when (num) {
                        42 -> 42
                        else -> return when (num) {
                            0 -> {
                                ((42))
                            }
                            else -> {
                                if (num % 17 == 0) {
                                    num.let { return@let 1 }
                                    num.let { return 42 }
                                }
                                42
                            }
                        }
                    })
                } else 42
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a function with another value in a return passes`() {
        val code = """
            fun test(num: Int, flag1: Boolean, flag2: Boolean): Int {
                if (flag1) {
                    return (0)
                }
                return if (flag2) {
                    (when (num) {
                        42 -> 42
                        else -> 42
                    })
                } else 42
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a function with another value in a non-local return passes`() {
        val code = """
            fun test(num: Int, flag: Boolean): Int {
                if (flag) {
                    num.let { return 0 }
                }
                return 42
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a return from a local function does not count`() {
        val code = """
            fun test(flag: Boolean): String {
                fun local(): String {
                    return "b"
                }
                if (flag) return "a"
                return "a"
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `an expression body with the same value in both branches is reported`() {
        val code = """
            fun test(flag: Boolean) = if (flag) "a" else "a"
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a function with one return passes`() {
        val code = """
            class Test {
                fun test(): Int {
                    return 0
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `an enum entry returned from every return is reported`() {
        val code = """
            enum class Color { RED, GREEN }

            fun test(flag: Boolean): Color {
                if (flag) return Color.RED
                return Color.RED
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a function that returns a variable passes`() {
        val code = """
            fun test(flag: Boolean, value: Int): Int {
                if (flag) return value
                return value
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `an open function passes`() {
        val code = """
            open class A {
                open fun test(flag: Boolean): String {
                    if (flag) {
                        return "foo"
                    } else {
                        return "foo"
                    }
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a final function in an open class is reported`() {
        val code = """
            open class A {
                fun test(flag: Boolean): String {
                    if (flag) {
                        return "foo"
                    } else {
                        return "foo"
                    }
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `an override passes`() {
        val code = """
            interface A {
                fun test(flag: Boolean): String
            }

            class B : A {
                override fun test(flag: Boolean): String {
                    if (flag) return "foo"
                    return "foo"
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a function that returns Unit passes`() {
        val code = """
            fun test(flag: Boolean) {
                if (flag) return Unit
                return Unit
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}

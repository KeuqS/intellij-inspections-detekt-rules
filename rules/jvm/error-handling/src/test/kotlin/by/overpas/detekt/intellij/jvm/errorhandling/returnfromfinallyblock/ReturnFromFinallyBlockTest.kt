package by.overpas.detekt.intellij.jvm.errorhandling.returnfromfinallyblock

import dev.detekt.api.Config
import dev.detekt.test.lint
import kotlin.test.Test
import kotlin.test.assertEquals

class ReturnFromFinallyBlockTest {

    private val sut = ReturnFromFinallyBlock(Config.empty)

    @Test
    fun `a return in a finally block is reported`() {
        val code = """
            fun foo() {
                try {
                } finally {
                    return
                }
            }
        """.trimIndent()

        val findings = sut.lint(code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a return from a local function in a finally block passes`() {
        val code = """
            fun foo() {
                try {
                } finally {
                    fun bar() {
                        return
                    }
                }
            }
        """.trimIndent()

        val findings = sut.lint(code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a return in a nested try block in a finally block is reported`() {
        val code = """
            fun foo() {
                try {
                } finally {
                    try {
                        return
                    } finally {
                    }
                }
            }
        """.trimIndent()

        val findings = sut.lint(code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a labeled return from a lambda over a finally block is reported`() {
        val code = """
            fun run(body: () -> Unit): Unit = body()

            fun foo() {
                run bar@{
                    try {
                    } finally {
                        return@bar
                    }
                }
            }
        """.trimIndent()

        val findings = sut.lint(code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a labeled return from a lambda in a finally block passes`() {
        val code = """
            fun run(body: () -> Unit): Unit = body()

            fun foo() {
                try {
                } finally {
                    run bar@{
                        try {
                            return@bar
                        } finally {
                        }
                    }
                }
            }
        """.trimIndent()

        val findings = sut.lint(code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a return with the call name label from a lambda in a finally block passes`() {
        val code = """
            fun foo(values: List<Int>) {
                try {
                } finally {
                    values.forEach {
                        if (it > 0) return@forEach
                    }
                    values.forEach({ value ->
                        if (value > 0) return@forEach
                    })
                }
            }
        """.trimIndent()

        val findings = sut.lint(code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a non-local return from a lambda in a finally block is reported`() {
        val code = """
            fun foo(values: List<Int>): Int {
                try {
                    return 0
                } finally {
                    values.forEach {
                        if (it > 0) return it
                    }
                }
            }
        """.trimIndent()

        val findings = sut.lint(code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a return labeled with the function name in a finally block is reported`() {
        val code = """
            fun foo() {
                try {
                } finally {
                    return@foo
                }
            }
        """.trimIndent()

        val findings = sut.lint(code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a return in an object in a finally block passes`() {
        val code = """
            fun foo() {
                try {
                } finally {
                    val runnable = object : Runnable {
                        override fun run() {
                            return
                        }
                    }
                    runnable.run()
                }
            }
        """.trimIndent()

        val findings = sut.lint(code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a return in a try block passes`() {
        val code = """
            fun foo(): Int {
                try {
                    return 1
                } catch (e: Exception) {
                    return 2
                } finally {
                    println()
                }
            }
        """.trimIndent()

        val findings = sut.lint(code)

        assertEquals(0, findings.size)
    }
}

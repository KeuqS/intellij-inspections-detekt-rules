package by.overpas.detekt.intellij.kotlin.redundantconstructs.canbeparameter

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class CanBeParameterTest {

    private val environment = createEnvironment()

    private val sut = CanBeParameter(Config.empty)

    @Test
    fun `a property of a private class used only in a property initializer is reported`() {
        val code = """
            private class UsedInProperty(val x: Int) {
                val y = x
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a private property used in a property initializer and in an init block is reported`() {
        val code = """
            class UsedInPropertyAndInitializer(private val x: Int) {
                val y = x

                init {
                    println(x)
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a private property used only in a super type call is reported`() {
        val code = """
            open class Base(s: String)

            class UsedInSuper(private val x: String) : Base(x)
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a private property used only in an init block is reported`() {
        val code = """
            class UsedInInitializer(private val x: Int) {
                var y: String

                init {
                    y = x.toString()
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a property of a private class used only in a super type call is reported`() {
        val code = """
            open class Base1(s: String)

            private class UsedInSuper(val bar123: String) : Base1(bar123)
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a var property of a private class used only in a property initializer is reported`() {
        val code = """
            private class UsedInProperty(var x: Int) {
                var y = x
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a vararg property of a private class used only in a property initializer is reported`() {
        val code = """
            private class Wrapper(vararg val x: Int) {
                val y = x
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a property of a local class used only in an init block is reported`() {
        val code = """
            fun test() {
                class UsedWithoutThisInInitProperty(val x: Int) {
                    init {
                        val y = x
                    }
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a private property used only in a property initializer passes`() {
        val code = """
            class UsedInProperty(private val x: Int) {
                val y = x
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a public property passes`() {
        val code = """
            class UsedInPublicProperty(val x: Int) {
                val y = x
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `an unused private property passes`() {
        val code = """
            class NonUsed(private val x: Int)
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a private property used in a function passes`() {
        val code = """
            class UsedInFunction(private val x: Int) {
                fun get() = x
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a private property used in a getter passes`() {
        val code = """
            class UsedInGetter(private val x: Int) {
                val y: Int
                    get() = x
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a private property used in a lazy delegate passes`() {
        val code = """
            class UsedInDelegate(private val x: Int) {
                val y: Int by lazy {
                    x * x
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a private property modified in an init block passes`() {
        val code = """
            class ModifiedInInit(private var x: Int) {
                init {
                    x += 2
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a private property used with this in an init block passes`() {
        val code = """
            class UsedWithThisInInitProperty(private val x: Int) {
                init {
                    val y = this.x
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a private property used as a callable reference passes`() {
        val code = """
            class UsedAsReference(private var baz: String) {
                var bar = ::baz
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a private property used in an object literal passes`() {
        val code = """
            class UsedInObjectLiteral(private val x: Int) {
                val y = object : Any() {
                    fun bar() = x
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a private property of a data class passes`() {
        val code = """
            data class UsedInData(private val x: Int) {
                val y = x
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a private property shadowed by a builder property passes`() {
        val code = $$"""
            class Bar(private val foo: String) {
                val property = foo.myDsl {
                    "DSL-local foo: $foo"
                }
            }

            class MyBuilder(foo: String) {
                val foo = "builder-local foo: $foo"
            }

            fun String.myDsl(init: MyBuilder.() -> String) =
                MyBuilder(this).run(init)
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}

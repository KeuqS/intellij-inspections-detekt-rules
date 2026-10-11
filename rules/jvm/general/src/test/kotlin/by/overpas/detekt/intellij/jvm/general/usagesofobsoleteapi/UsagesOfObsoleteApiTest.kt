package by.overpas.detekt.intellij.jvm.general.usagesofobsoleteapi

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class UsagesOfObsoleteApiTest {

    private val environment = createEnvironment()

    private val sut = UsagesOfObsoleteApi(Config.empty)

    @Test
    fun `a direct usage of an obsolete method is reported`() {
        val code = """
            import foo.A

            class B {
                fun f(a: A) {
                    a.f()
                }
            }
        """.trimIndent()
        val dependency = """
            package foo

            import org.jetbrains.annotations.ApiStatus

            open class A {
                @ApiStatus.Obsolete
                open fun f() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, dependency)

        assertEquals(1, findings.size)
    }

    @Test
    fun `overrides of an obsolete method are reported`() {
        val code = """
            import foo.A
            import org.jetbrains.annotations.ApiStatus

            class C : A() {
                override fun f() {}
            }

            @ApiStatus.Obsolete
            class D : A() {
                override fun f() {}
            }
        """.trimIndent()
        val dependency = """
            package foo

            import org.jetbrains.annotations.ApiStatus

            open class A {
                @ApiStatus.Obsolete
                open fun f() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, dependency)

        assertEquals(2, findings.size)
    }

    @Test
    fun `a generic reference to an obsolete interface is reported`() {
        val code = """
            import foo.I

            class U {
                fun u(i: I<Int>) = i
            }
        """.trimIndent()
        val dependency = """
            package foo

            import org.jetbrains.annotations.ApiStatus

            @ApiStatus.Obsolete
            interface I<T>
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, dependency)

        assertEquals(1, findings.size)
    }

    @Test
    fun `constructor calls of obsolete classes and constructors are reported`() {
        val code = """
            import foo.ObsoleteClass
            import foo.ObsoleteConstructor

            fun test() {
                ObsoleteClass()
                ObsoleteConstructor(1)
                ObsoleteConstructor()
            }
        """.trimIndent()
        val dependency = """
            package foo

            import org.jetbrains.annotations.ApiStatus

            @ApiStatus.Obsolete
            class ObsoleteClass

            class ObsoleteConstructor() {
                @ApiStatus.Obsolete
                constructor(value: Int) : this()
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, dependency)

        assertEquals(2, findings.size)
    }

    @Test
    fun `an obsolete property access is reported`() {
        val code = """
            import foo.A

            fun test(a: A) = a.value
        """.trimIndent()
        val dependency = """
            package foo

            import org.jetbrains.annotations.ApiStatus

            class A {
                @ApiStatus.Obsolete
                val value = 0
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, dependency)

        assertEquals(1, findings.size)
    }

    @Test
    fun `only the reference to an obsolete class is reported and not its members`() {
        val code = """
            import foo.A

            fun test(a: Any) = (a as? A)?.f()
        """.trimIndent()
        val dependency = """
            package foo

            import org.jetbrains.annotations.ApiStatus

            @ApiStatus.Obsolete
            class A {
                fun f() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, dependency)

        assertEquals(1, findings.size)
    }

    @Test
    fun `imports and usages in the same file pass`() {
        val code = """
            import org.jetbrains.annotations.ApiStatus

            class A {
                @ApiStatus.Obsolete
                fun f() {}

                fun g() = f()
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}

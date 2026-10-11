package by.overpas.detekt.intellij.jvm.codematurity

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertEquals

class MarkedForRemovalTest {

    private val environment = createEnvironment(listOf(Path("src/test/resources/MarkedForRemoval")))

    private val sut = MarkedForRemoval(Config.empty)

    @Test
    fun `a constructor call of a class marked for removal is reported`() {
        val code = """
            package test

            fun main() {
                B()
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a call of a method of a class marked for removal is reported`() {
        val code = """
            package test

            fun test(c: C) {
                c.bar()
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a call of an override of a method of a class that is not deprecated passes`() {
        val code = """
            package test

            fun test(c: C) {
                c.foo()
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a call of a method marked for removal since a version is reported`() {
        val code = """
            package test

            fun test(d: D) {
                d.old("a")
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a call of a deprecated method that is not marked for removal passes`() {
        val code = """
            package test

            fun test(d: D) {
                d.plain()
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a field marked for removal is reported`() {
        val code = """
            package test

            fun test() = D.OLD
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a property access to a getter marked for removal is reported`() {
        val code = """
            package test

            fun test(d: D) = d.name
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a JDK method marked for removal is reported`() {
        val code = """
            fun test() {
                Runtime.getRuntime().runFinalization()
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `an import of a class marked for removal is reported`() {
        val code = """
            import test.B

            fun test(b: B) = b
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(2, findings.size)
    }

    @Test
    fun `an override of a method marked for removal is reported`() {
        val code = """
            package test

            class E : D() {
                override fun hook() = Unit
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a Kotlin declaration marked for removal used from another class is reported`() {
        val code = """
            class Old {
                @java.lang.Deprecated(forRemoval = true)
                fun old(value: String) = value
            }

            class New {
                fun test(old: Old) = old.old("a")
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a Kotlin declaration marked for removal used in the same outermost class passes`() {
        val code = """
            class Old {
                @java.lang.Deprecated(forRemoval = true)
                fun old() = Unit

                inner class Inner {
                    fun test() = old()
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a usage in the same outermost class is reported when it is not ignored`() {
        val code = """
            class Old {
                @java.lang.Deprecated(forRemoval = true)
                fun old() = Unit

                fun test() = old()
            }
        """.trimIndent()
        val sut = MarkedForRemoval(TestConfig("isSameOutermostClassIgnored" to false))

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `an assignment to a property with a setter that is not marked for removal passes`() {
        val code = """
            package test

            fun test(d: D) {
                d.name = "a"
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}

package by.overpas.detekt.intellij.jvm.general

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class SerializableHasSerialVersionUIDFieldTest {

    private val environment = createEnvironment()

    private val sut = SerializableHasSerialVersionUIDField(Config.empty)

    @Test
    fun `a serializable class without serialVersionUID is reported`() {
        val code = """
            import java.io.Serializable

            class Foo : Serializable { }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a subclass of a serializable class is reported`() {
        val code = """
            open class Base : java.io.Serializable {
                companion object {
                    private const val serialVersionUID: Long = 1L
                }
            }

            class Child : Base()
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a serializable object literal is reported`() {
        val code = """
            import java.io.Serializable

            val foo = object : Serializable {}
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a serializable object literal passes when anonymous classes are ignored`() {
        val code = """
            import java.io.Serializable

            val foo = object : Serializable {}
        """.trimIndent()
        val sut = SerializableHasSerialVersionUIDField(TestConfig("isAnonymousClassIgnored" to true))

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a class with serialVersionUID in the companion object passes`() {
        val code = """
            import java.io.Serializable

            class Foo : Serializable {
                companion object {
                    private const val serialVersionUID: Long = 1L
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `an object with serialVersionUID passes`() {
        val code = """
            import java.io.Serializable

            object Foo : Serializable {
                private const val serialVersionUID: Long = 1L
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a class with writeReplace passes`() {
        val code = """
            import java.io.Serializable

            class Foo : Serializable {
                private fun writeReplace(): Any = this
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `interfaces enums and annotations pass`() {
        val code = """
            import java.io.Serializable

            interface Foo : Serializable

            enum class Bar { A, B }

            annotation class Baz
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a class that is not serializable passes`() {
        val code = """
            class Foo
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a subclass of an ignored class passes`() {
        val code = """
            open class Base : java.io.Serializable {
                companion object {
                    private const val serialVersionUID: Long = 1L
                }
            }

            class Child : Base()
        """.trimIndent()
        val sut = SerializableHasSerialVersionUIDField(TestConfig("ignoredSuperClasses" to listOf("Base")))

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a directly serializable subclass of an ignored class is reported`() {
        val code = """
            open class Base

            class Child : Base(), java.io.Serializable
        """.trimIndent()
        val sut = SerializableHasSerialVersionUIDField(TestConfig("ignoredSuperClasses" to listOf("Base")))

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }
}

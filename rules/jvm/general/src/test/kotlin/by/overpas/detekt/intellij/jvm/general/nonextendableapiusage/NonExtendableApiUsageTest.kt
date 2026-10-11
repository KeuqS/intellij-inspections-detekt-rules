package by.overpas.detekt.intellij.jvm.general.nonextendableapiusage

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class NonExtendableApiUsageTest {

    private val environment = createEnvironment()

    private val sut = NonExtendableApiUsage(Config.empty)

    @Test
    fun `extensions of non-extendable library classes and interfaces are reported`() {
        val code = """
            import by.overpas.detekt.intellij.jvm.general.apistatus.ApiStatusNonExtendableLibrary.NonExtendableClass
            import by.overpas.detekt.intellij.jvm.general.apistatus.ApiStatusNonExtendableLibrary.NonExtendableInterface

            class Inheritor : NonExtendableClass()

            class Implementor : NonExtendableInterface

            interface InterfaceInheritor : NonExtendableInterface
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(3, findings.size)
    }

    @Test
    fun `anonymous objects that extend non-extendable library classes are reported`() {
        val code = """
            import by.overpas.detekt.intellij.jvm.general.apistatus.ApiStatusNonExtendableLibrary.NonExtendableClass
            import by.overpas.detekt.intellij.jvm.general.apistatus.ApiStatusNonExtendableLibrary.NonExtendableInterface

            fun anonymousClasses() {
                object : NonExtendableClass() {}
                object : NonExtendableInterface {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(2, findings.size)
    }

    @Test
    fun `an extension of a non-extendable nested library class is reported`() {
        val code = """
            import by.overpas.detekt.intellij.jvm.general.apistatus.ApiStatusNonExtendableLibrary.NonExtendableNestedOwner

            class Inheritor : NonExtendableNestedOwner.NonExtendableNested()
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `an extension of a nested class of a non-extendable library class passes`() {
        val code = """
            import by.overpas.detekt.intellij.jvm.general.apistatus.ApiStatusNonExtendableLibrary.NonExtendableNestedClassOwner

            class Inheritor : NonExtendableNestedClassOwner.NestedClass()
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `an override of a non-extendable library method is reported`() {
        val code = """
            import by.overpas.detekt.intellij.jvm.general.apistatus.ApiStatusNonExtendableLibrary.NonExtendableMethodOwner

            class Overrider : NonExtendableMethodOwner() {
                override fun doNotOverride() = 1

                override fun mayOverride() = 1
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a usage of a non-extendable library class outside a supertype list passes`() {
        val code = """
            import by.overpas.detekt.intellij.jvm.general.apistatus.ApiStatusNonExtendableLibrary.NonExtendableClass

            class Holder(val value: NonExtendableClass) : Comparable<NonExtendableClass> {
                override fun compareTo(other: NonExtendableClass) = 0
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `an extension of a non-extendable class of the analyzed sources passes`() {
        val code = """
            import org.jetbrains.annotations.ApiStatus

            @ApiStatus.NonExtendable
            open class Base

            class Inheritor : Base()
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `annotations on declarations that cannot be extended are reported`() {
        val code = """
            import org.jetbrains.annotations.ApiStatus
            import org.jetbrains.annotations.ApiStatus.NonExtendable

            @ApiStatus.NonExtendable
            fun foo() {}

            class NotOpenClass {
                @NonExtendable
                fun foo() {}
            }

            open class OpenClass {
                @NonExtendable
                private fun foo() {}

                @NonExtendable
                fun bar() {}

                @NonExtendable
                open fun thisCouldBeActuallyExtended() {}

                companion object {
                    @NonExtendable
                    private fun blah() {}
                }
            }

            @NonExtendable
            class SomeClass

            @NonExtendable
            enum class EnumClass { BLACK, WHITE }

            @NonExtendable
            data class DataClass(val name: String)
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(8, findings.size)
    }

    @Test
    fun `a redundant annotation on an open method of a non-extendable class is reported`() {
        val code = """
            import org.jetbrains.annotations.ApiStatus.NonExtendable

            @NonExtendable
            open class SomeOpenClass {
                @NonExtendable
                open fun someOpenMethod() {}

                open fun someAnotherOpenMethod() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }
}

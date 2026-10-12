package by.overpas.detekt.intellij.jvm.general.overrideonly

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class OverrideOnlyTest {

    private val environment = createEnvironment()

    private val sut = OverrideOnly(Config.empty)

    @Test
    fun `invocations of override-only library methods are reported`() {
        val code = """
            import by.overpas.detekt.intellij.jvm.general.apistatus.ApiStatusOverrideOnlyLibrary.OverrideOnlyClass
            import by.overpas.detekt.intellij.jvm.general.apistatus.ApiStatusOverrideOnlyLibrary.OverrideOnlyMethodInterface
            import by.overpas.detekt.intellij.jvm.general.apistatus.ApiStatusOverrideOnlyLibrary.OverrideOnlyMethodOwner

            fun invocations(
                owner: OverrideOnlyMethodOwner,
                methodInterface: OverrideOnlyMethodInterface,
                overrideOnlyClass: OverrideOnlyClass,
            ) {
                owner.overrideOnlyMethod()
                methodInterface.implementOnlyMethod()
                overrideOnlyClass.overrideOnlyMethod()
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(3, findings.size)
    }

    @Test
    fun `references to override-only library methods are reported`() {
        val code = """
            import by.overpas.detekt.intellij.jvm.general.apistatus.ApiStatusOverrideOnlyLibrary.OverrideOnlyClass
            import by.overpas.detekt.intellij.jvm.general.apistatus.ApiStatusOverrideOnlyLibrary.OverrideOnlyMethodInterface

            fun methodReferences() {
                println(OverrideOnlyMethodInterface::implementOnlyMethod)
                println(OverrideOnlyClass::overrideOnlyMethod)
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(2, findings.size)
    }

    @Test
    fun `invocations of static and final methods of override-only library classes pass`() {
        val code = """
            import by.overpas.detekt.intellij.jvm.general.apistatus.ApiStatusOverrideOnlyLibrary.OverrideOnlyClass

            fun invocations(overrideOnlyClass: OverrideOnlyClass) {
                OverrideOnlyClass.staticMethod()
                overrideOnlyClass.finalMethod()
                println(OverrideOnlyClass.Companion::staticMethod)
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `overrides of override-only library methods pass`() {
        val code = """
            import by.overpas.detekt.intellij.jvm.general.apistatus.ApiStatusOverrideOnlyLibrary.OverrideOnlyClass
            import by.overpas.detekt.intellij.jvm.general.apistatus.ApiStatusOverrideOnlyLibrary.OverrideOnlyMethodInterface

            class Inheritor : OverrideOnlyClass() {
                override fun overrideOnlyMethod() {}
            }

            class Implementor : OverrideOnlyMethodInterface {
                override fun implementOnlyMethod() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `super and delegate calls inside the overriding method pass`() {
        val code = """
            import by.overpas.detekt.intellij.jvm.general.apistatus.ApiStatusOverrideOnlyLibrary.OverrideOnlyMethodOwner

            class Inheritor(private val delegate: OverrideOnlyMethodOwner) : OverrideOnlyMethodOwner() {
                override fun overrideOnlyMethod(): Int {
                    super.overrideOnlyMethod()
                    return delegate.overrideOnlyMethod()
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `super and delegate calls inside other methods are reported`() {
        val code = """
            import by.overpas.detekt.intellij.jvm.general.apistatus.ApiStatusOverrideOnlyLibrary.OverrideOnlyMethodOwner

            class Inheritor(private val delegate: OverrideOnlyMethodOwner) : OverrideOnlyMethodOwner() {
                override fun overrideOnlyMethod(value: Int): Int {
                    super.overrideOnlyMethod()
                    delegate.overrideOnlyMethod()
                    return value
                }

                fun notOverrideOnlyMethod() {
                    super.overrideOnlyMethod()
                    delegate.overrideOnlyMethod()
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(4, findings.size)
    }

    @Test
    fun `an invocation of an override-only method of the analyzed sources passes`() {
        val code = """
            import org.jetbrains.annotations.ApiStatus

            open class Base {
                @ApiStatus.OverrideOnly
                open fun method() {}
            }

            fun test(base: Base) = base.method()
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `annotations on declarations that cannot be overridden are reported`() {
        val code = """
            import org.jetbrains.annotations.ApiStatus
            import org.jetbrains.annotations.ApiStatus.OverrideOnly

            @ApiStatus.OverrideOnly
            fun foo() {}

            class NotOpenClass {
                @OverrideOnly
                fun foo() {}
            }

            open class OpenClass {
                @OverrideOnly
                open fun overridableFun() {}

                @OverrideOnly
                fun notOverridableFun() {}

                @OverrideOnly
                private fun foo() {}

                companion object {
                    @OverrideOnly
                    private fun blah() {}
                }
            }

            @OverrideOnly
            open class NonFinalClass {
                fun blah() {}
            }

            @OverrideOnly
            class FinalClass {
                fun blah() {}
            }

            @OverrideOnly
            enum class SomeEnum { BLACK, WHITE }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(7, findings.size)
    }

    @Test
    fun `a redundant annotation on an open method of an override-only class is reported`() {
        val code = """
            import org.jetbrains.annotations.ApiStatus.OverrideOnly

            @OverrideOnly
            open class SomeOpenClass {
                @OverrideOnly
                open fun foo() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }
}

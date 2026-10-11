package by.overpas.detekt.intellij.jvm.general

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class UnstableApiUsageTest {

    private val environment = createEnvironment()

    private val sut = UnstableApiUsage(Config.empty)

    @Test
    fun `usages of an experimental library class are reported`() {
        val code = """
            import by.overpas.detekt.intellij.jvm.general.apistatus.ApiStatusUnstableLibrary.ExperimentalClass

            fun test(): String {
                val instance: ExperimentalClass = ExperimentalClass()
                return instance.nonAnnotatedField
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(4, findings.size)
    }

    @Test
    fun `usages of experimental members of a stable library class are reported`() {
        val code = """
            import by.overpas.detekt.intellij.jvm.general.apistatus.ApiStatusUnstableLibrary.StableClass

            fun test(): String {
                val instance = StableClass("")
                instance.annotatedMethod()
                return instance.annotatedField
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(3, findings.size)
    }

    @Test
    fun `usages of stable members of a stable library class pass`() {
        val code = """
            import by.overpas.detekt.intellij.jvm.general.apistatus.ApiStatusUnstableLibrary.StableClass

            fun test(): String {
                val instance = StableClass()
                instance.nonAnnotatedMethod()
                return instance.nonAnnotatedField
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `usages of experimental enums and enum entries are reported`() {
        val code = """
            import by.overpas.detekt.intellij.jvm.general.apistatus.ApiStatusUnstableLibrary.ExperimentalEnum
            import by.overpas.detekt.intellij.jvm.general.apistatus.ApiStatusUnstableLibrary.StableEnum

            fun test() {
                println(ExperimentalEnum.VALUE)
                println(StableEnum.ANNOTATED_VALUE)
                println(StableEnum.NON_ANNOTATED_VALUE)
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(3, findings.size)
    }

    @Test
    fun `an override of an experimental library method is reported`() {
        val code = """
            import by.overpas.detekt.intellij.jvm.general.apistatus.ApiStatusUnstableLibrary.StableClass

            open class DirectOverride : StableClass() {
                override fun annotatedMethod() = 1
            }

            class IndirectOverride : DirectOverride() {
                override fun annotatedMethod() = 1
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `usages of library apis with experimental types in the signature are reported`() {
        val code = """
            import by.overpas.detekt.intellij.jvm.general.apistatus.ApiStatusUnstableLibrary.ExperimentalSignatureOwner

            fun test(owner: ExperimentalSignatureOwner) {
                println(owner.field)
                owner.parameterType(null)
                owner.returnType()
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(3, findings.size)
    }

    @Test
    fun `a library class with an experimental type parameter bound is reported`() {
        val code = """
            import by.overpas.detekt.intellij.jvm.general.apistatus.ApiStatusUnstableLibrary.ClassWithExperimentalTypeInSignature

            class Usage : ClassWithExperimentalTypeInSignature<Nothing>()
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(2, findings.size)
    }

    @Test
    fun `usages of apis scheduled for removal are reported with the version`() {
        val code = """
            import by.overpas.detekt.intellij.jvm.general.apistatus.ApiStatusUnstableLibrary.ScheduledForRemovalClass
            import by.overpas.detekt.intellij.jvm.general.apistatus.ApiStatusUnstableLibrary.ScheduledForRemovalWithoutVersion

            fun test(first: ScheduledForRemovalClass, second: ScheduledForRemovalWithoutVersion) {
                first.method()
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(3, findings.size)
    }

    @Test
    fun `imports are reported when they are not ignored`() {
        val code = """
            import by.overpas.detekt.intellij.jvm.general.apistatus.ApiStatusUnstableLibrary.ExperimentalClass

            fun test() = Unit
        """.trimIndent()
        val sut = UnstableApiUsage(TestConfig("isInsideImportsIgnored" to false))

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `apis declared in the analyzed sources pass by default`() {
        val code = """
            import org.jetbrains.annotations.ApiStatus

            @ApiStatus.Experimental
            class Foo

            fun test() = Foo()
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `apis declared in the analyzed sources are reported when they are not ignored`() {
        val code = """
            import foo.Foo

            fun test() = Foo()
        """.trimIndent()
        val dependency = """
            package foo

            import org.jetbrains.annotations.ApiStatus

            @ApiStatus.Internal
            class Foo
        """.trimIndent()
        val sut = UnstableApiUsage(TestConfig("isApiDeclaredInThisProjectIgnored" to false))

        val findings = sut.lintWithContext(environment, code, dependency)

        assertEquals(2, findings.size)
    }

    @Test
    fun `members of the same file with unstable types in the signature pass`() {
        val code = """
            import by.overpas.detekt.intellij.jvm.general.apistatus.ApiStatusUnstableLibrary.ExperimentalClass

            class Foo {
                private fun returnType(): ExperimentalClass? = null

                fun test() {
                    returnType()
                }
            }
        """.trimIndent()
        val sut = UnstableApiUsage(TestConfig("isApiDeclaredInThisProjectIgnored" to false))

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `usages of annotations not in the list pass`() {
        val code = """
            import by.overpas.detekt.intellij.jvm.general.apistatus.ApiStatusUnstableLibrary.ExperimentalClass

            fun test() = ExperimentalClass()
        """.trimIndent()
        val sut = UnstableApiUsage(
            TestConfig("unstableApiAnnotations" to listOf("org.jetbrains.annotations.ApiStatus.Internal")),
        )

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}

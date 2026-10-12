package by.overpas.detekt.intellij.jvm.general.unstabletypeusedinsignature

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class UnstableTypeUsedInSignatureTest {

    private val environment = createEnvironment()

    private val sut = UnstableTypeUsedInSignature(Config.empty)

    @Test
    fun `properties with unstable types are reported`() {
        val code = """
            import test.ExperimentalClass

            class UnstableTypeUsedInSignature {
                var field: ExperimentalClass? = null

                var arrayField: Array<ExperimentalClass>? = null

                var listField: List<ExperimentalClass>? = null
            }
        """.trimIndent()
        val dependency = """
            package test

            import org.jetbrains.annotations.ApiStatus

            @ApiStatus.Experimental
            class ExperimentalClass
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, dependency)

        assertEquals(3, findings.size)
    }

    @Test
    fun `functions with unstable types in the signature are reported`() {
        val code = """
            import test.ExperimentalClass

            class UnstableTypeUsedInSignature {
                fun methodWithExperimentalParam(param: ExperimentalClass) = param.hashCode()

                fun methodWithExperimentalParamList(param: List<ExperimentalClass>) = param.size

                fun methodWithExperimentalReturnType(): ExperimentalClass? = null

                fun <T : ExperimentalClass> methodWithUnstableTypeParameter(): T? = null
            }
        """.trimIndent()
        val dependency = """
            package test

            import org.jetbrains.annotations.ApiStatus

            @ApiStatus.Experimental
            class ExperimentalClass
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, dependency)

        assertEquals(4, findings.size)
    }

    @Test
    fun `a class with an unstable type parameter bound is reported`() {
        val code = """
            import test.ExperimentalClass

            class UnstableTypeUsedInTypeParameter<T : ExperimentalClass>
        """.trimIndent()
        val dependency = """
            package test

            import org.jetbrains.annotations.ApiStatus

            @ApiStatus.Experimental
            open class ExperimentalClass
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, dependency)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a type nested in an unstable class is reported`() {
        val code = """
            import test.ExperimentalClass

            fun nested(value: ExperimentalClass.Nested) = value
        """.trimIndent()
        val dependency = """
            package test

            import org.jetbrains.annotations.ApiStatus

            @ApiStatus.Internal
            class ExperimentalClass {
                class Nested
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, dependency)

        assertEquals(1, findings.size)
    }

    @Test
    fun `declarations that are already marked pass`() {
        val code = """
            import org.jetbrains.annotations.ApiStatus
            import test.ExperimentalClass

            @ApiStatus.Experimental
            class NoWarningsAlreadyMarked {
                var field: ExperimentalClass? = null

                fun methodWithExperimentalParam(param: ExperimentalClass) = param.hashCode()
            }

            class NoWarningsMembersAlreadyMarked {
                @ApiStatus.Experimental
                var field: ExperimentalClass? = null

                @ApiStatus.Experimental
                fun methodWithExperimentalReturnType(): ExperimentalClass? = null
            }
        """.trimIndent()
        val dependency = """
            package test

            import org.jetbrains.annotations.ApiStatus

            @ApiStatus.Experimental
            class ExperimentalClass
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, dependency)

        assertEquals(0, findings.size)
    }

    @Test
    fun `inaccessible declarations pass`() {
        val code = """
            import test.ExperimentalClass

            class NoWarningsInaccessible {
                private val privateField: ExperimentalClass? = null

                private fun privateMethodWithParam(param: ExperimentalClass) = param.hashCode()

                fun anonymousClassNoWarnings() = object {
                    var anonymousField: ExperimentalClass? = null

                    fun anonymousMethodWithReturnType(): ExperimentalClass? = null
                }
            }

            private class PrivateKotlinClass<T : ExperimentalClass>
        """.trimIndent()
        val dependency = """
            package test

            import org.jetbrains.annotations.ApiStatus

            @ApiStatus.Experimental
            open class ExperimentalClass
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, dependency)

        assertEquals(0, findings.size)
    }

    @Test
    fun `annotations not in the list pass`() {
        val code = """
            import test.ExperimentalClass

            fun method(param: ExperimentalClass) = param
        """.trimIndent()
        val dependency = """
            package test

            import org.jetbrains.annotations.ApiStatus

            @ApiStatus.Experimental
            class ExperimentalClass
        """.trimIndent()
        val sut = UnstableTypeUsedInSignature(
            TestConfig("unstableApiAnnotations" to listOf("org.jetbrains.annotations.ApiStatus.Internal")),
        )

        val findings = sut.lintWithContext(environment, code, dependency)

        assertEquals(0, findings.size)
    }
}

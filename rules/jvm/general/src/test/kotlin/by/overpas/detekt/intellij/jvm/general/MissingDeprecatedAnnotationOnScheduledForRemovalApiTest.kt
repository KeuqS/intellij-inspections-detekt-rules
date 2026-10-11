package by.overpas.detekt.intellij.jvm.general

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class MissingDeprecatedAnnotationOnScheduledForRemovalApiTest {

    private val environment = createEnvironment()

    private val sut = MissingDeprecatedAnnotationOnScheduledForRemovalApi(Config.empty)

    @Test
    fun `a class, a property and a function scheduled for removal without deprecation are reported`() {
        val code = """
            import org.jetbrains.annotations.ApiStatus

            @ApiStatus.ScheduledForRemoval
            class Warnings {
                @ApiStatus.ScheduledForRemoval
                val field: Int = 0

                @ApiStatus.ScheduledForRemoval
                fun method() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(3, findings.size)
    }

    @Test
    fun `a top-level function scheduled for removal without deprecation is reported`() {
        val code = """
            import org.jetbrains.annotations.ApiStatus

            @ApiStatus.ScheduledForRemoval
            fun method() {}
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a constructor scheduled for removal without deprecation is reported`() {
        val code = """
            import org.jetbrains.annotations.ApiStatus

            class Foo {
                @ApiStatus.ScheduledForRemoval
                constructor()
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `declarations scheduled for removal with kotlin deprecation pass`() {
        val code = """
            import org.jetbrains.annotations.ApiStatus

            @ApiStatus.ScheduledForRemoval
            @Deprecated("reason")
            class NoWarnings {
                @ApiStatus.ScheduledForRemoval
                @Deprecated("reason")
                val field: Int = 0

                @ApiStatus.ScheduledForRemoval
                @Deprecated("reason")
                fun method() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a declaration scheduled for removal with java deprecation passes`() {
        val code = """
            import org.jetbrains.annotations.ApiStatus

            @ApiStatus.ScheduledForRemoval
            @java.lang.Deprecated
            fun method() {}
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a deprecated declaration without schedule for removal passes`() {
        val code = """
            @Deprecated("reason")
            fun method() {}
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}

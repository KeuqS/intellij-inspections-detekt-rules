package by.overpas.detekt.intellij.jvm.general.mustalreadyberemovedapi

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class MustAlreadyBeRemovedApiTest {

    private val environment = createEnvironment()

    private val sut = MustAlreadyBeRemovedApi(TestConfig("currentVersion" to "3.0"))

    @Test
    fun `declarations scheduled for removal in an earlier version are reported`() {
        val code = """
            import org.jetbrains.annotations.ApiStatus

            @ApiStatus.ScheduledForRemoval(inVersion = "2.0")
            @Deprecated("")
            class Warnings {
                @ApiStatus.ScheduledForRemoval(inVersion = "2.0")
                @Deprecated("")
                var field: String? = null

                @ApiStatus.ScheduledForRemoval(inVersion = "2.0")
                @Deprecated("")
                fun method() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(3, findings.size)
    }

    @Test
    fun `a function scheduled for removal in an earlier version is reported`() {
        val code = """
            import org.jetbrains.annotations.ApiStatus

            @ApiStatus.ScheduledForRemoval(inVersion = "2.0")
            @Deprecated("")
            fun method() {}
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a function scheduled for removal in the current version is reported`() {
        val code = """
            import org.jetbrains.annotations.ApiStatus

            @ApiStatus.ScheduledForRemoval(inVersion = "3.0")
            @Deprecated("")
            fun method() {}
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `declarations scheduled for removal in a later version pass`() {
        val code = """
            import org.jetbrains.annotations.ApiStatus

            @Deprecated("")
            @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
            class NoWarnings {
                @Deprecated("")
                @ApiStatus.ScheduledForRemoval(inVersion = "5.0")
                var field: String? = null

                @Deprecated("")
                @ApiStatus.ScheduledForRemoval(inVersion = "3.0.1")
                fun method() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a release candidate of the current version is reported`() {
        val code = """
            import org.jetbrains.annotations.ApiStatus

            @ApiStatus.ScheduledForRemoval(inVersion = "3.0-rc1")
            @Deprecated("")
            fun method() {}
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a version with a higher numeric part is not reported`() {
        val code = """
            import org.jetbrains.annotations.ApiStatus

            @ApiStatus.ScheduledForRemoval(inVersion = "10.0")
            @Deprecated("")
            fun method() {}
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a declaration without a removal version passes`() {
        val code = """
            import org.jetbrains.annotations.ApiStatus

            @ApiStatus.ScheduledForRemoval
            @Deprecated("")
            fun method() {}
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `nothing is reported without a current version`() {
        val code = """
            import org.jetbrains.annotations.ApiStatus

            @ApiStatus.ScheduledForRemoval(inVersion = "2.0")
            @Deprecated("")
            fun method() {}
        """.trimIndent()
        val sut = MustAlreadyBeRemovedApi(Config.empty)

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}

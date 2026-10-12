package by.overpas.detekt.intellij.android.composepreview.previewapilevelmustbevalid

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class PreviewApiLevelMustBeValidTest {

    private val environment = createEnvironment()

    private val sut = PreviewApiLevelMustBeValid(Config.empty)

    @Test
    fun `an api level above the default range is reported on the argument`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            @Preview(name = "Preview", apiLevel = 1000000)
            fun MyPreview() {}
        """.trimIndent()
        val preview = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview(val name: String = "", val apiLevel: Int = -1)
        """.trimIndent()
        val expected = listOf("apiLevel = 1000000")

        val findings = sut.lintWithContext(environment, code, preview)
        val reportedTexts = findings.map { finding ->
            finding.entity.location.text.run { code.substring(start, end) }
        }

        assertEquals(expected, reportedTexts)
    }

    @Test
    fun `an api level below the default range is reported`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            @Preview(apiLevel = 21)
            fun MyPreview() {}
        """.trimIndent()
        val preview = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview(val name: String = "", val apiLevel: Int = -1)
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(1, findings.size)
    }

    @Test
    fun `api levels at the bounds of the default range pass`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            @Preview(apiLevel = 22)
            @Preview(apiLevel = 37)
            fun MyPreview() {}
        """.trimIndent()
        val preview = """
            package androidx.compose.ui.tooling.preview

            @Repeatable
            annotation class Preview(val name: String = "", val apiLevel: Int = -1)
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(0, findings.size)
    }

    @Test
    fun `an invalid constant on an annotation class is reported`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            private const val BAD_API_LEVEL = 0

            @Preview(apiLevel = BAD_API_LEVEL)
            annotation class BadAnnotation
        """.trimIndent()
        val preview = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview(val name: String = "", val apiLevel: Int = -1)
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a multipreview annotation parameter out of range passes`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            @Preview(apiLevel = 30)
            annotation class GoodAnnotation(val apiLevel: Int = 0)

            @GoodAnnotation(apiLevel = 0)
            fun MyPreview() {}
        """.trimIndent()
        val preview = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview(val name: String = "", val apiLevel: Int = -1)
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(0, findings.size)
    }

    @Test
    fun `api levels out of the configured range are reported`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            @Preview(apiLevel = 30)
            @Preview(apiLevel = 36)
            fun MyPreview() {}
        """.trimIndent()
        val preview = """
            package androidx.compose.ui.tooling.preview

            @Repeatable
            annotation class Preview(val name: String = "", val apiLevel: Int = -1)
        """.trimIndent()
        val sut = PreviewApiLevelMustBeValid(TestConfig("minApiLevel" to 31, "maxApiLevel" to 35))

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(2, findings.size)
    }

    @Test
    fun `an api level in the configured range passes`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            @Preview(apiLevel = 40)
            fun MyPreview() {}
        """.trimIndent()
        val preview = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview(val name: String = "", val apiLevel: Int = -1)
        """.trimIndent()
        val sut = PreviewApiLevelMustBeValid(TestConfig("maxApiLevel" to 40))

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(0, findings.size)
    }
}

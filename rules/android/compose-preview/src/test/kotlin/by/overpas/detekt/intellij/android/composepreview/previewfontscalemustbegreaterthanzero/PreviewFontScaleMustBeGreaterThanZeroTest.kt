package by.overpas.detekt.intellij.android.composepreview.previewfontscalemustbegreaterthanzero

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class PreviewFontScaleMustBeGreaterThanZeroTest {

    private val environment = createEnvironment()

    private val sut = PreviewFontScaleMustBeGreaterThanZero(Config.empty)

    @Test
    fun `a negative font scale is reported on the argument`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            @Preview(name = "Preview", fontScale = -2f)
            fun MyPreview() {}
        """.trimIndent()
        val preview = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview(val name: String = "", val fontScale: Float = 1f)
        """.trimIndent()
        val expected = listOf("fontScale = -2f")

        val findings = sut.lintWithContext(environment, code, preview)
        val reportedTexts = findings.map { finding ->
            finding.entity.location.text.run { code.substring(start, end) }
        }

        assertEquals(expected, reportedTexts)
    }

    @Test
    fun `a zero font scale constant on an annotation class is reported`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            private const val BAD_FONT_SCALE = 0f

            @Preview(fontScale = BAD_FONT_SCALE)
            annotation class BadAnnotation
        """.trimIndent()
        val preview = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview(val name: String = "", val fontScale: Float = 1f)
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a positive font scale passes`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            @Preview(fontScale = 0.5f)
            fun MyPreview() {}
        """.trimIndent()
        val preview = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview(val name: String = "", val fontScale: Float = 1f)
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a multipreview annotation parameter that is not positive passes`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            @Preview(fontScale = 1f)
            annotation class GoodAnnotation(val fontScale: Float = 0f)

            @GoodAnnotation(fontScale = 0f)
            fun MyPreview() {}
        """.trimIndent()
        val preview = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview(val name: String = "", val fontScale: Float = 1f)
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(0, findings.size)
    }
}

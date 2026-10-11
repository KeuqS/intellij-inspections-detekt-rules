package by.overpas.detekt.intellij.android.composepreview.composepreviewdimensionrespectslimit

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class ComposePreviewDimensionRespectsLimitTest {

    private val environment = createEnvironment()

    private val sut = ComposePreviewDimensionRespectsLimit(Config.empty)

    @Test
    fun `a width above the limit is reported on the argument`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            @Preview(name = "Wide", widthDp = 3001)
            fun MyPreview() {}
        """.trimIndent()
        val preview = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview(val name: String = "", val widthDp: Int = -1, val heightDp: Int = -1)
        """.trimIndent()
        val expected = listOf("widthDp = 3001")

        val findings = sut.lintWithContext(environment, code, preview)
        val reportedTexts = findings.map { finding ->
            finding.entity.location.text.run { code.substring(start, end) }
        }

        assertEquals(expected, reportedTexts)
    }

    @Test
    fun `a height below the limit is reported`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            @Preview(heightDp = 0)
            fun MyPreview() {}
        """.trimIndent()
        val preview = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview(val name: String = "", val widthDp: Int = -1, val heightDp: Int = -1)
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(1, findings.size)
    }

    @Test
    fun `dimensions at the limits pass`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            @Preview(widthDp = 1, heightDp = 3000)
            fun MyPreview() {}
        """.trimIndent()
        val preview = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview(val name: String = "", val widthDp: Int = -1, val heightDp: Int = -1)
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(0, findings.size)
    }

    @Test
    fun `an annotation with both dimensions out of limits is reported once on the height`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            @Preview(heightDp = 3001, widthDp = 3001)
            fun MyPreview() {}
        """.trimIndent()
        val preview = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview(val name: String = "", val widthDp: Int = -1, val heightDp: Int = -1)
        """.trimIndent()
        val expected = listOf("heightDp = 3001")

        val findings = sut.lintWithContext(environment, code, preview)
        val reportedTexts = findings.map { finding ->
            finding.entity.location.text.run { code.substring(start, end) }
        }

        assertEquals(expected, reportedTexts)
    }

    @Test
    fun `a constant out of limits on an annotation class is reported`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            private const val BAD_WIDTH = 3001

            @Preview(widthDp = BAD_WIDTH)
            annotation class BadAnnotation
        """.trimIndent()
        val preview = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview(val name: String = "", val widthDp: Int = -1, val heightDp: Int = -1)
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a multipreview annotation parameter out of limits passes`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            @Preview(widthDp = 3000)
            annotation class GoodAnnotation(val widthDp: Int = 3001)

            @GoodAnnotation(widthDp = 3001)
            fun MyPreview() {}
        """.trimIndent()
        val preview = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview(val name: String = "", val widthDp: Int = -1, val heightDp: Int = -1)
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(0, findings.size)
    }

    @Test
    fun `an annotation with the same simple name from another package passes`() {
        val code = """
            import com.example.Preview

            @Preview(widthDp = 3001)
            fun MyPreview() {}
        """.trimIndent()
        val preview = """
            package com.example

            annotation class Preview(val widthDp: Int = -1)
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(0, findings.size)
    }
}

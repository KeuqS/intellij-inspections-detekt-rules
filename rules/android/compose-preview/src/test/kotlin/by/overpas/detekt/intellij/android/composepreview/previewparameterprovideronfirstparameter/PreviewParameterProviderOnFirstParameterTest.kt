package by.overpas.detekt.intellij.android.composepreview.previewparameterprovideronfirstparameter

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class PreviewParameterProviderOnFirstParameterTest {

    private val environment = createEnvironment()

    private val sut = PreviewParameterProviderOnFirstParameter(Config.empty)

    @Test
    fun `a preview parameter on the second parameter is reported`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview
            import androidx.compose.ui.tooling.preview.PreviewParameter

            @Preview
            fun MyPreview(
                a: Int,
                @PreviewParameter b: Int,
                c: Int,
            ) {}
        """.trimIndent()
        val tooling = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview

            annotation class PreviewParameter
        """.trimIndent()
        val expected = listOf(7)

        val lines = sut.lintWithContext(environment, code, tooling).map { it.entity.location.source.line }

        assertEquals(expected, lines)
    }

    @Test
    fun `only the first misplaced preview parameter is reported`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview
            import androidx.compose.ui.tooling.preview.PreviewParameter

            @Preview
            fun MyPreview(
                a: Int,
                @PreviewParameter b: Int,
                @PreviewParameter c: Int,
            ) {}
        """.trimIndent()
        val tooling = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview

            annotation class PreviewParameter
        """.trimIndent()
        val expected = listOf(7)

        val lines = sut.lintWithContext(environment, code, tooling).map { it.entity.location.source.line }

        assertEquals(expected, lines)
    }

    @Test
    fun `a preview parameter on the third parameter of a multipreview function is reported`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview
            import androidx.compose.ui.tooling.preview.PreviewParameter

            @Preview
            annotation class MyAnnotation

            @MyAnnotation
            fun MyPreview(a: Int, b: Int, @PreviewParameter c: Int, d: Int) {}
        """.trimIndent()
        val tooling = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview

            annotation class PreviewParameter
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tooling)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a preview parameter on the first parameter passes`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview
            import androidx.compose.ui.tooling.preview.PreviewParameter

            @Preview
            fun MyPreview(@PreviewParameter a: Int, @PreviewParameter b: Int) {}
        """.trimIndent()
        val tooling = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview

            annotation class PreviewParameter
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tooling)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a preview function without a preview parameter passes`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            @Preview
            fun MyPreview(a: Int, b: Int) {}
        """.trimIndent()
        val tooling = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tooling)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a function without a preview annotation passes`() {
        val code = """
            import androidx.compose.ui.tooling.preview.PreviewParameter

            fun NotPreview(a: Int, @PreviewParameter b: Int) {}
        """.trimIndent()
        val tooling = """
            package androidx.compose.ui.tooling.preview

            annotation class PreviewParameter
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tooling)

        assertEquals(0, findings.size)
    }
}

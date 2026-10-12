package by.overpas.detekt.intellij.android.composepreview.previewmultipleparameterproviders

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class PreviewMultipleParameterProvidersTest {

    private val environment = createEnvironment()

    private val sut = PreviewMultipleParameterProviders(Config.empty)

    @Test
    fun `the second preview parameter of a preview function is reported`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview
            import androidx.compose.ui.tooling.preview.PreviewParameter

            @Preview
            fun MyPreview(
                @PreviewParameter a: Int,
                @PreviewParameter b: Int,
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
    fun `only the second of three preview parameters is reported`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview
            import androidx.compose.ui.tooling.preview.PreviewParameter

            @Preview
            fun MyPreview(
                @PreviewParameter a: Int,
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
    fun `the second preview parameter of a function with a multipreview annotation is reported`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview
            import androidx.compose.ui.tooling.preview.PreviewParameter

            @Preview
            annotation class MyAnnotation

            @MyAnnotation
            fun MyPreview(@PreviewParameter a: Int, @PreviewParameter b: Int) {}
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
    fun `a function with two preview annotations is reported once`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview
            import androidx.compose.ui.tooling.preview.PreviewParameter

            @Preview
            annotation class MyAnnotation

            @MyAnnotation
            @Preview
            fun MyPreview(@PreviewParameter a: Int, @PreviewParameter b: Int) {}
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
    fun `a preview function with one preview parameter passes`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview
            import androidx.compose.ui.tooling.preview.PreviewParameter

            @Preview
            fun MyPreview(@PreviewParameter a: Int, b: Int = 1) {}
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
    fun `a function without a preview annotation passes`() {
        val code = """
            import androidx.compose.ui.tooling.preview.PreviewParameter

            fun NotPreview(@PreviewParameter a: Int, @PreviewParameter b: Int) {}
        """.trimIndent()
        val tooling = """
            package androidx.compose.ui.tooling.preview

            annotation class PreviewParameter
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tooling)

        assertEquals(0, findings.size)
    }
}

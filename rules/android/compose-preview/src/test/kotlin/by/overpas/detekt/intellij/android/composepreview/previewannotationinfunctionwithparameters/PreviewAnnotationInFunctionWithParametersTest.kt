package by.overpas.detekt.intellij.android.composepreview.previewannotationinfunctionwithparameters

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class PreviewAnnotationInFunctionWithParametersTest {

    private val environment = createEnvironment()

    private val sut = PreviewAnnotationInFunctionWithParameters(Config.empty)

    @Test
    fun `a preview function with a parameter without a default value is reported`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            @Preview
            fun MyPreview(a: Int) {}
        """.trimIndent()
        val tooling = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tooling)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a preview function with a default value after a parameter without one is reported`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            @Preview
            fun MyPreview(a: Int, b: String = "hello") {}
        """.trimIndent()
        val tooling = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tooling)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a preview function with only default values passes`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            @Preview
            fun MyPreview(b: String = "hello") {}
        """.trimIndent()
        val tooling = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tooling)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a preview function with a preview parameter and a default value passes`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview
            import androidx.compose.ui.tooling.preview.PreviewParameter

            @Preview
            fun MyPreview(@PreviewParameter a: Int, b: String = "hello") {}
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
    fun `a preview function with a preview parameter and a parameter without a default value is reported`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview
            import androidx.compose.ui.tooling.preview.PreviewParameter

            @Preview
            fun MyPreview(@PreviewParameter a: Int, b: String) {}
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
    fun `a function with a multipreview annotation and a parameter without a default value is reported`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            @Preview
            annotation class MyAnnotation

            @MyAnnotation
            fun MyPreview(a: Int) {}
        """.trimIndent()
        val tooling = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tooling)

        assertEquals(1, findings.size)
    }

    @Test
    fun `every preview annotation of the function is reported`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            @Preview
            annotation class MyAnnotation

            @MyAnnotation
            @Preview
            fun MyPreview(a: Int) {}
        """.trimIndent()
        val tooling = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview
        """.trimIndent()
        val expected = listOf(6, 7)

        val lines = sut.lintWithContext(environment, code, tooling).map { it.entity.location.source.line }

        assertEquals(expected, lines)
    }

    @Test
    fun `a function with an annotation that is not a multipreview passes`() {
        val code = """
            annotation class MyEmptyAnnotation

            @MyEmptyAnnotation
            fun NotMultiPreview(a: Int) {}
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `an annotation class with parameters and a preview annotation passes`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            @Preview
            annotation class MyAnnotation(val a: Int)
        """.trimIndent()
        val tooling = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tooling)

        assertEquals(0, findings.size)
    }
}

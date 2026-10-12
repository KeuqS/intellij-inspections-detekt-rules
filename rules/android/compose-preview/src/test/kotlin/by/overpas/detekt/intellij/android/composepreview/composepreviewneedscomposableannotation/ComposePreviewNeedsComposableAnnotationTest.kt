package by.overpas.detekt.intellij.android.composepreview.composepreviewneedscomposableannotation

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class ComposePreviewNeedsComposableAnnotationTest {

    private val environment = createEnvironment()

    private val sut = ComposePreviewNeedsComposableAnnotation(Config.empty)

    @Test
    fun `a preview function without the composable annotation is reported`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            @Preview
            fun MyPreview() {}
        """.trimIndent()
        val preview = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a composable preview function passes`() {
        val code = """
            import androidx.compose.runtime.Composable
            import androidx.compose.ui.tooling.preview.Preview

            @Preview
            @Composable
            fun MyPreview() {}
        """.trimIndent()
        val preview = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview
        """.trimIndent()
        val runtime = """
            package androidx.compose.runtime

            annotation class Composable
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview, runtime)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a function with a multipreview annotation without the composable annotation is reported`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            @Preview
            annotation class LightPreview

            @LightPreview
            annotation class ThemePreviews

            @ThemePreviews
            fun MyPreview() {}
        """.trimIndent()
        val preview = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(1, findings.size)
    }

    @Test
    fun `an annotation with the same simple name from another package passes`() {
        val code = """
            import com.example.Preview

            @Preview
            fun MyPreview() {}
        """.trimIndent()
        val preview = """
            package com.example

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(0, findings.size)
    }

    @Test
    fun `annotation classes that refer to each other pass`() {
        val code = """
            @Second
            annotation class First

            @First
            annotation class Second

            @First
            fun MyPreview() {}
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}

package by.overpas.detekt.intellij.android.composepreview.previewwrapperusage

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class PreviewWrapperUsageTest {

    private val environment = createEnvironment()

    private val sut = PreviewWrapperUsage(Config.empty)

    @Test
    fun `a preview wrapper on a composable preview function passes`() {
        val code = """
            import androidx.compose.runtime.Composable
            import androidx.compose.ui.tooling.preview.Preview
            import androidx.compose.ui.tooling.preview.PreviewWrapper

            @Composable
            @Preview
            @PreviewWrapper
            fun MyPreview() {}
        """.trimIndent()
        val tooling = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview

            annotation class PreviewWrapper
        """.trimIndent()
        val runtime = """
            package androidx.compose.runtime

            annotation class Composable
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tooling, runtime)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a preview wrapper on a preview function without the composable annotation is reported`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview
            import androidx.compose.ui.tooling.preview.PreviewWrapper

            @Preview
            @PreviewWrapper
            fun MyPreview() {}
        """.trimIndent()
        val tooling = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview

            annotation class PreviewWrapper
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tooling)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a preview wrapper on a composable function without a preview annotation is reported`() {
        val code = """
            import androidx.compose.runtime.Composable
            import androidx.compose.ui.tooling.preview.PreviewWrapper

            @Composable
            @PreviewWrapper
            fun MyPreview() {}
        """.trimIndent()
        val tooling = """
            package androidx.compose.ui.tooling.preview

            annotation class PreviewWrapper
        """.trimIndent()
        val runtime = """
            package androidx.compose.runtime

            annotation class Composable
        """.trimIndent()
        val expected = listOf(5)

        val lines = sut.lintWithContext(environment, code, tooling, runtime).map { it.entity.location.source.line }

        assertEquals(expected, lines)
    }

    @Test
    fun `a preview wrapper on a function without both annotations is reported`() {
        val code = """
            import androidx.compose.ui.tooling.preview.PreviewWrapper

            @PreviewWrapper
            fun MyPreview() {}
        """.trimIndent()
        val tooling = """
            package androidx.compose.ui.tooling.preview

            annotation class PreviewWrapper
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tooling)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a preview wrapper on a composable function with a multipreview annotation passes`() {
        val code = """
            import androidx.compose.runtime.Composable
            import androidx.compose.ui.tooling.preview.Preview
            import androidx.compose.ui.tooling.preview.PreviewWrapper

            @Preview
            annotation class MyMultiPreview

            @Composable
            @MyMultiPreview
            @PreviewWrapper
            fun MyPreview() {}
        """.trimIndent()
        val tooling = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview

            annotation class PreviewWrapper
        """.trimIndent()
        val runtime = """
            package androidx.compose.runtime

            annotation class Composable
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tooling, runtime)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a duplicate preview wrapper on an annotation class is reported with the annotation graph`() {
        val code = """
            import androidx.compose.ui.tooling.preview.PreviewWrapper

            @PreviewWrapper
            @PreviewWrapper
            annotation class MyAnnotation
        """.trimIndent()
        val tooling = """
            package androidx.compose.ui.tooling.preview

            @Repeatable
            annotation class PreviewWrapper
        """.trimIndent()
        val expected = listOf(4, 5)

        val lines = sut.lintWithContext(environment, code, tooling).map { it.entity.location.source.line }

        assertEquals(expected, lines)
    }

    @Test
    fun `one preview wrapper on an annotation class passes`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview
            import androidx.compose.ui.tooling.preview.PreviewWrapper

            @Preview
            @PreviewWrapper
            annotation class MyMultiPreview
        """.trimIndent()
        val tooling = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview

            annotation class PreviewWrapper
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tooling)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a composable function with two preview wrappers in its multipreviews is reported`() {
        val code = """
            import androidx.compose.runtime.Composable
            import androidx.compose.ui.tooling.preview.Preview
            import androidx.compose.ui.tooling.preview.PreviewWrapper

            @Preview
            @PreviewWrapper
            annotation class MultiPreview1

            @Preview
            @PreviewWrapper
            annotation class MultiPreview2

            @Composable
            @MultiPreview1
            @MultiPreview2
            fun MyPreview() {}
        """.trimIndent()
        val tooling = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview

            annotation class PreviewWrapper
        """.trimIndent()
        val runtime = """
            package androidx.compose.runtime

            annotation class Composable
        """.trimIndent()
        val expected = listOf(16)

        val lines = sut.lintWithContext(environment, code, tooling, runtime).map { it.entity.location.source.line }

        assertEquals(expected, lines)
    }

    @Test
    fun `a composable function with a direct preview wrapper and one in its multipreview is reported`() {
        val code = """
            import androidx.compose.runtime.Composable
            import androidx.compose.ui.tooling.preview.Preview
            import androidx.compose.ui.tooling.preview.PreviewWrapper

            @Preview
            @PreviewWrapper
            annotation class MultiPreview1

            @Composable
            @PreviewWrapper
            @MultiPreview1
            fun MyPreview() {}
        """.trimIndent()
        val tooling = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview

            annotation class PreviewWrapper
        """.trimIndent()
        val runtime = """
            package androidx.compose.runtime

            annotation class Composable
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tooling, runtime)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a multipreview reached twice in the graph counts its preview wrapper once`() {
        val code = """
            import androidx.compose.runtime.Composable
            import androidx.compose.ui.tooling.preview.Preview
            import androidx.compose.ui.tooling.preview.PreviewWrapper

            @Preview
            @PreviewWrapper
            annotation class MultiPreview1

            @MultiPreview1
            annotation class MultiPreview2

            @Composable
            @MultiPreview1
            @MultiPreview2
            fun MyPreview() {}
        """.trimIndent()
        val tooling = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview

            annotation class PreviewWrapper
        """.trimIndent()
        val runtime = """
            package androidx.compose.runtime

            annotation class Composable
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tooling, runtime)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a function without the composable annotation is not checked for preview wrappers in its graph`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview
            import androidx.compose.ui.tooling.preview.PreviewWrapper

            @Preview
            @PreviewWrapper
            annotation class MultiPreview1

            @Preview
            @PreviewWrapper
            annotation class MultiPreview2

            @MultiPreview1
            @MultiPreview2
            fun MyPreview() {}
        """.trimIndent()
        val tooling = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview

            annotation class PreviewWrapper
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tooling)

        assertEquals(0, findings.size)
    }

    @Test
    fun `annotation classes with more than one preview wrapper in their graph are reported`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview
            import androidx.compose.ui.tooling.preview.PreviewWrapper

            @Preview
            @PreviewWrapper
            annotation class MultiPreview1

            @Preview
            @PreviewWrapper
            annotation class MultiPreview2

            @MultiPreview1
            @MultiPreview2
            annotation class MyAnnotation1

            @PreviewWrapper
            @MultiPreview1
            annotation class MyAnnotation2

            @MultiPreview1
            annotation class MyAnnotation3
        """.trimIndent()
        val tooling = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview

            annotation class PreviewWrapper
        """.trimIndent()
        val expected = listOf(14, 18)

        val lines = sut.lintWithContext(environment, code, tooling).map { it.entity.location.source.line }

        assertEquals(expected, lines)
    }
}

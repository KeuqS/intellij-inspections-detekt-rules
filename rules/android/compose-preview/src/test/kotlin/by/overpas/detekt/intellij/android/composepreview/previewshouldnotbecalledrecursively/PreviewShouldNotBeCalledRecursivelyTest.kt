package by.overpas.detekt.intellij.android.composepreview.previewshouldnotbecalledrecursively

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class PreviewShouldNotBeCalledRecursivelyTest {

    private val environment = createEnvironment()

    private val sut = PreviewShouldNotBeCalledRecursively(Config.empty)

    @Test
    fun `a preview that calls itself in a lambda is reported on the call`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            @Preview
            fun Preview1() {
                Wrapper {
                    Preview1()
                }
            }

            fun Wrapper(content: () -> Unit) {
                content()
            }
        """.trimIndent()
        val preview = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview
        """.trimIndent()
        val expected = listOf("Preview1()")

        val findings = sut.lintWithContext(environment, code, preview)
        val reportedTexts = findings.map { finding ->
            finding.entity.location.text.run { code.substring(start, end) }
        }

        assertEquals(expected, reportedTexts)
    }

    @Test
    fun `a function without a preview annotation that calls itself passes`() {
        val code = """
            fun Composable1() {
                Wrapper {
                    Composable1()
                }
            }

            fun Wrapper(content: () -> Unit) {
                content()
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a multipreview function that calls itself is reported`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            @Preview
            annotation class LightPreview

            @LightPreview
            fun MyPreview() {
                MyPreview()
            }
        """.trimIndent()
        val preview = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a call to another function with the same name is reported`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            object Screens {
                fun MyPreview(title: String) {}
            }

            @Preview
            fun MyPreview() {
                Screens.MyPreview("title")
            }
        """.trimIndent()
        val preview = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a call through an import alias is reported`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview
            import MyPreview as Self

            @Preview
            fun MyPreview() {
                Self()
            }
        """.trimIndent()
        val preview = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a preview that calls a function with another name passes`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            @Preview
            fun MyPreview() {
                MyScreen()
            }

            fun MyScreen() {}
        """.trimIndent()
        val preview = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a call inside a local function of a preview is checked against the local function name`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            @Preview
            fun MyPreview() {
                fun content() {
                    MyPreview()
                }
            }
        """.trimIndent()
        val preview = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(0, findings.size)
    }
}

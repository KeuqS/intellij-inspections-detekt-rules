package by.overpas.detekt.intellij.android.glancepreview.glancepreviewmustbetoplevelfunction

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class GlancePreviewMustBeTopLevelFunctionTest {

    private val environment = createEnvironment()

    private val sut = GlancePreviewMustBeTopLevelFunction(Config.empty)

    @Test
    fun `a top level glance preview function passes`() {
        val code = """
            import androidx.glance.preview.Preview

            @Preview
            fun MyPreview() {}
        """.trimIndent()
        val preview = """
            package androidx.glance.preview

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(0, findings.size)
    }

    @Test
    fun `glance previews in a top level class with a default constructor pass`() {
        val code = """
            import androidx.glance.preview.Preview

            class Previews {
                @Preview
                fun MyPreview() {}

                @Preview
                private fun MyPrivatePreview() {}
            }

            class SecondaryConstructorPreviews(val size: Int) {
                constructor() : this(1)

                @Preview
                fun MyPreview() {}
            }
        """.trimIndent()
        val preview = """
            package androidx.glance.preview

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(0, findings.size)
    }

    @Test
    fun `local glance preview functions are reported`() {
        val code = """
            import androidx.glance.preview.Preview

            @Preview
            fun TopLevelPreview() {
                @Preview
                fun NestedPreview() {
                    @Preview
                    fun SuperNestedPreview() {}
                }
            }
        """.trimIndent()
        val preview = """
            package androidx.glance.preview

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(2, findings.size)
    }

    @Test
    fun `a glance preview in a nested class is reported`() {
        val code = """
            import androidx.glance.preview.Preview

            class Outer {
                class Nested {
                    @Preview
                    fun MyPreview() {}
                }
            }
        """.trimIndent()
        val preview = """
            package androidx.glance.preview

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a glance preview in a class without a default constructor is reported`() {
        val code = """
            import androidx.glance.preview.Preview

            class Previews(val size: Int) {
                @Preview
                fun MyPreview() {}
            }
        """.trimIndent()
        val preview = """
            package androidx.glance.preview

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a glance preview in an object is reported`() {
        val code = """
            import androidx.glance.preview.Preview

            object Previews {
                @Preview
                fun MyPreview() {}
            }
        """.trimIndent()
        val preview = """
            package androidx.glance.preview

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(1, findings.size)
    }

    @Test
    fun `each preview and multipreview annotation of a misplaced function is reported`() {
        val code = """
            import androidx.glance.preview.Preview

            annotation class Empty

            @Preview
            annotation class WidgetPreview

            class Previews(val size: Int) {
                @Preview
                @Empty
                @WidgetPreview
                fun MyPreview() {}
            }
        """.trimIndent()
        val preview = """
            package androidx.glance.preview

            annotation class Preview
        """.trimIndent()
        val expected = listOf("@Preview", "@WidgetPreview")

        val findings = sut.lintWithContext(environment, code, preview)
        val reported = findings.map { finding -> finding.entity.location.text.run { code.substring(start, end) } }

        assertEquals(expected, reported)
    }

    @Test
    fun `a misplaced function without a glance preview annotation passes`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            class Previews(val size: Int) {
                @Preview
                fun MyPreview() {}
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

package by.overpas.detekt.intellij.android.composepreview.composepreviewmustbetoplevelfunction

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class ComposePreviewMustBeTopLevelFunctionTest {

    private val environment = createEnvironment()

    private val sut = ComposePreviewMustBeTopLevelFunction(Config.empty)

    @Test
    fun `a top level preview function passes`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            @Preview
            fun MyPreview() {}
        """.trimIndent()
        val tooling = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tooling)

        assertEquals(0, findings.size)
    }

    @Test
    fun `every preview annotation of a local preview function is reported`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            annotation class MyEmptyAnnotation

            @Preview
            annotation class MyAnnotation

            fun TopLevel() {
                @MyEmptyAnnotation
                @MyAnnotation
                @Preview
                fun LocalPreview() {}
            }
        """.trimIndent()
        val tooling = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview
        """.trimIndent()
        val expected = listOf(10, 11)

        val lines = sut.lintWithContext(environment, code, tooling).map { it.entity.location.source.line }

        assertEquals(expected, lines)
    }

    @Test
    fun `a preview function in a top level class without a constructor passes`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            class MyClass {
                @Preview
                private fun MyPreview() {}
            }
        """.trimIndent()
        val tooling = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tooling)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a preview function in a top level class with a no-parameter secondary constructor passes`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            class MyClass(val i: Int) {
                constructor() : this(0)

                @Preview
                fun MyPreview() {}
            }
        """.trimIndent()
        val tooling = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tooling)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a preview function in a class with constructor parameters is reported`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            class MyClass(i: Int) {
                @Preview
                fun MyPreview() {}
            }
        """.trimIndent()
        val tooling = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tooling)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a preview function in a class with only default constructor arguments is reported`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            class MyClass(i: Int = 0) {
                @Preview
                fun MyPreview() {}
            }
        """.trimIndent()
        val tooling = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tooling)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a preview function in a nested class is reported`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            class Outer {
                class Nested {
                    @Preview
                    fun MyPreview() {}
                }
            }
        """.trimIndent()
        val tooling = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tooling)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a preview function in a top level object is reported`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            object MyObject {
                @Preview
                fun MyPreview() {}
            }
        """.trimIndent()
        val tooling = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tooling)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a local preview function in a class method is reported`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            class MyClass {
                fun method() {
                    @Preview
                    fun LocalPreview() {}
                }
            }
        """.trimIndent()
        val tooling = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tooling)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a local function without a preview annotation passes`() {
        val code = """
            annotation class MyEmptyAnnotation

            fun TopLevel() {
                @MyEmptyAnnotation
                fun Local() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(0, findings.size)
    }
}

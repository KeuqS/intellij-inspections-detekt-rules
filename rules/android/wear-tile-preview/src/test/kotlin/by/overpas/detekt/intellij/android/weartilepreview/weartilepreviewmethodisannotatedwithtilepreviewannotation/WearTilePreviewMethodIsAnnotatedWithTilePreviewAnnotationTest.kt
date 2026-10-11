package by.overpas.detekt.intellij.android.weartilepreview.weartilepreviewmethodisannotatedwithtilepreviewannotation

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class WearTilePreviewMethodIsAnnotatedWithTilePreviewAnnotationTest {

    private val environment = createEnvironment()

    private val sut = WearTilePreviewMethodIsAnnotatedWithTilePreviewAnnotation(Config.empty)

    @Test
    fun `a preview annotation from another package on a tile preview function is reported`() {
        val code = """
            import androidx.wear.tiles.tooling.preview.TilePreviewData
            import com.example.Preview

            @Preview
            fun MyPreview() = TilePreviewData()
        """.trimIndent()
        val tiles = """
            package androidx.wear.tiles.tooling.preview

            annotation class Preview(val name: String = "")

            class TilePreviewData
        """.trimIndent()
        val other = """
            package com.example

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tiles, other)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a compose preview annotation on a tile preview function with a context parameter is reported`() {
        val code = """
            import android.content.Context
            import androidx.compose.ui.tooling.preview.Preview
            import androidx.wear.tiles.tooling.preview.TilePreviewData

            @Preview
            fun MyPreview(context: Context) = TilePreviewData()
        """.trimIndent()
        val tiles = """
            package androidx.wear.tiles.tooling.preview

            annotation class Preview(val name: String = "")

            class TilePreviewData
        """.trimIndent()
        val context = """
            package android.content

            open class Context {
                val isUiContext: Boolean = false
                val resources: Any = Any()

                fun getString(id: Int): String = ""
            }
        """.trimIndent()
        val compose = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tiles, context, compose)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a multipreview annotation that refers to a preview annotation from another package is reported`() {
        val code = """
            import androidx.wear.tiles.tooling.preview.TilePreviewData
            import com.example.Preview

            @Preview
            annotation class InvalidMultiPreview

            @InvalidMultiPreview
            fun MyPreview() = TilePreviewData()
        """.trimIndent()
        val tiles = """
            package androidx.wear.tiles.tooling.preview

            annotation class Preview(val name: String = "")

            class TilePreviewData
        """.trimIndent()
        val other = """
            package com.example

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tiles, other)
        val reported = findings.single().entity.location.text
            .run { code.substring(start, end) }

        assertEquals("@InvalidMultiPreview", reported)
    }

    @Test
    fun `each invalid annotation of a tile preview function is reported`() {
        val code = """
            import androidx.wear.tiles.tooling.preview.TilePreviewData
            import com.example.Preview

            @Preview
            annotation class InvalidMultiPreview

            @Preview
            @InvalidMultiPreview
            fun MyPreview() = TilePreviewData()
        """.trimIndent()
        val tiles = """
            package androidx.wear.tiles.tooling.preview

            annotation class Preview(val name: String = "")

            class TilePreviewData
        """.trimIndent()
        val other = """
            package com.example

            annotation class Preview
        """.trimIndent()
        val expected = listOf("@Preview", "@InvalidMultiPreview")

        val findings = sut.lintWithContext(environment, code, tiles, other)
        val reported = findings.map { finding -> finding.entity.location.text.run { code.substring(start, end) } }

        assertEquals(expected, reported)
    }

    @Test
    fun `a tile preview annotation passes`() {
        val code = """
            import androidx.wear.tiles.tooling.preview.Preview
            import androidx.wear.tiles.tooling.preview.TilePreviewData

            @Preview
            annotation class TileMultiPreview

            @Preview
            @TileMultiPreview
            fun MyPreview() = TilePreviewData()
        """.trimIndent()
        val tiles = """
            package androidx.wear.tiles.tooling.preview

            annotation class Preview(val name: String = "")

            class TilePreviewData
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tiles)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a function without the tile preview signature passes`() {
        val code = """
            import com.example.Preview

            @Preview
            fun MyPreview() = Unit
        """.trimIndent()
        val other = """
            package com.example

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, other)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a tile preview function without annotations passes`() {
        val code = """
            import androidx.wear.tiles.tooling.preview.TilePreviewData

            fun MyPreview() = TilePreviewData()
        """.trimIndent()
        val tiles = """
            package androidx.wear.tiles.tooling.preview

            annotation class Preview(val name: String = "")

            class TilePreviewData
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tiles)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a local function with the tile preview signature passes`() {
        val code = """
            import androidx.wear.tiles.tooling.preview.TilePreviewData
            import com.example.Preview

            fun outer() {
                @Preview
                fun MyPreview() = TilePreviewData()
            }
        """.trimIndent()
        val tiles = """
            package androidx.wear.tiles.tooling.preview

            annotation class Preview(val name: String = "")

            class TilePreviewData
        """.trimIndent()
        val other = """
            package com.example

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tiles, other)

        assertEquals(0, findings.size)
    }
}

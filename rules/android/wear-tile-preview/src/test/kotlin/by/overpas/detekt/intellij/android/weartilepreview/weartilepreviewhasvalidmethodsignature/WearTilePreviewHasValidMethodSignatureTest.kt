package by.overpas.detekt.intellij.android.weartilepreview.weartilepreviewhasvalidmethodsignature

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class WearTilePreviewHasValidMethodSignatureTest {

    private val environment = createEnvironment()

    private val sut = WearTilePreviewHasValidMethodSignature(Config.empty)

    @Test
    fun `a tile preview without a return type is reported on the name`() {
        val code = """
            import androidx.wear.tiles.tooling.preview.Preview

            @Preview
            fun invalidSignatureNoReturnType() {}
        """.trimIndent()
        val tiles = """
            package androidx.wear.tiles.tooling.preview

            annotation class Preview(val name: String = "")

            class TilePreviewData
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tiles)
        val reported = findings.single().entity.location.text
            .run { code.substring(start, end) }

        assertEquals("invalidSignatureNoReturnType", reported)
    }

    @Test
    fun `a tile preview with a wrong return type is reported`() {
        val code = """
            import androidx.wear.tiles.tooling.preview.Preview

            @Preview
            fun invalidSignatureWrongReturnType() = "some string"
        """.trimIndent()
        val tiles = """
            package androidx.wear.tiles.tooling.preview

            annotation class Preview(val name: String = "")

            class TilePreviewData
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tiles)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a tile preview with an invalid parameter is reported on the parameter list`() {
        val code = """
            import androidx.wear.tiles.tooling.preview.Preview
            import androidx.wear.tiles.tooling.preview.TilePreviewData

            @Preview
            fun invalidSignatureWithInvalidParameter(parameter: Int) = TilePreviewData()
        """.trimIndent()
        val tiles = """
            package androidx.wear.tiles.tooling.preview

            annotation class Preview(val name: String = "")

            class TilePreviewData
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tiles)
        val reported = findings.single().entity.location.text
            .run { code.substring(start, end) }

        assertEquals("(parameter: Int)", reported)
    }

    @Test
    fun `a tile preview with too many parameters is reported`() {
        val code = """
            import android.content.Context
            import androidx.wear.tiles.tooling.preview.Preview
            import androidx.wear.tiles.tooling.preview.TilePreviewData

            @Preview
            fun invalidSignatureWithTooManyParameters(context: Context, other: Int) = TilePreviewData()
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

        val findings = sut.lintWithContext(environment, code, tiles, context)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a tile preview with a wrong return type and an invalid parameter is reported twice`() {
        val code = """
            import androidx.wear.tiles.tooling.preview.Preview

            @Preview
            fun invalidSignature(parameter: Int) {}
        """.trimIndent()
        val tiles = """
            package androidx.wear.tiles.tooling.preview

            annotation class Preview(val name: String = "")

            class TilePreviewData
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tiles)

        assertEquals(2, findings.size)
    }

    @Test
    fun `a function with a tile multipreview annotation and a wrong return type is reported`() {
        val code = """
            import androidx.wear.tiles.tooling.preview.Preview

            @Preview
            annotation class TileMultiPreview

            @TileMultiPreview
            fun invalidSignature() {}
        """.trimIndent()
        val tiles = """
            package androidx.wear.tiles.tooling.preview

            annotation class Preview(val name: String = "")

            class TilePreviewData
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tiles)

        assertEquals(1, findings.size)
    }

    @Test
    fun `valid tile preview signatures pass`() {
        val code = """
            import android.content.Context
            import androidx.wear.tiles.tooling.preview.Preview
            import androidx.wear.tiles.tooling.preview.TilePreviewData

            @Preview
            fun validPreviewSignature() = TilePreviewData()

            @Preview
            fun validPreviewSignatureWithContext(context: Context): TilePreviewData = TilePreviewData()
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

        val findings = sut.lintWithContext(environment, code, tiles, context)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a function without a tile preview annotation passes`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            @Preview
            fun MyPreview(parameter: Int) {}
        """.trimIndent()
        val compose = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, compose)

        assertEquals(0, findings.size)
    }
}

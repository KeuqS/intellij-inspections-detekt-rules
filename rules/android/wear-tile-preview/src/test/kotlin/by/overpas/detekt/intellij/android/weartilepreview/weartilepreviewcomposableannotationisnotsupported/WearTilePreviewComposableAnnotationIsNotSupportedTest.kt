package by.overpas.detekt.intellij.android.weartilepreview.weartilepreviewcomposableannotationisnotsupported

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class WearTilePreviewComposableAnnotationIsNotSupportedTest {

    private val environment = createEnvironment()

    private val sut = WearTilePreviewComposableAnnotationIsNotSupported(Config.empty)

    @Test
    fun `a composable tile preview is reported`() {
        val code = """
            import androidx.compose.runtime.Composable
            import androidx.wear.tiles.tooling.preview.Preview
            import androidx.wear.tiles.tooling.preview.TilePreviewData

            @Preview
            @Composable
            fun MyPreview() = TilePreviewData()
        """.trimIndent()
        val tiles = """
            package androidx.wear.tiles.tooling.preview

            annotation class Preview(val name: String = "")

            class TilePreviewData
        """.trimIndent()
        val runtime = """
            package androidx.compose.runtime

            annotation class Composable
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tiles, runtime)
        val reported = findings.single().entity.location.text
            .run { code.substring(start, end) }

        assertEquals("@Composable", reported)
    }

    @Test
    fun `a composable tile preview with a tile multipreview annotation is reported`() {
        val code = """
            import android.content.Context
            import androidx.compose.runtime.Composable
            import androidx.wear.tiles.tooling.preview.Preview
            import androidx.wear.tiles.tooling.preview.TilePreviewData

            @Preview
            annotation class TileMultiPreview

            @TileMultiPreview
            @Composable
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
        val runtime = """
            package androidx.compose.runtime

            annotation class Composable
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tiles, context, runtime)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a composable function with the tile preview signature without a tile preview annotation passes`() {
        val code = """
            import androidx.compose.runtime.Composable
            import androidx.wear.tiles.tooling.preview.TilePreviewData

            @Composable
            fun MyPreview() = TilePreviewData()
        """.trimIndent()
        val tiles = """
            package androidx.wear.tiles.tooling.preview

            annotation class Preview(val name: String = "")

            class TilePreviewData
        """.trimIndent()
        val runtime = """
            package androidx.compose.runtime

            annotation class Composable
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tiles, runtime)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a tile preview without the composable annotation passes`() {
        val code = """
            import androidx.wear.tiles.tooling.preview.Preview
            import androidx.wear.tiles.tooling.preview.TilePreviewData

            @Preview
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
    fun `a composable function with a tile preview annotation and another signature passes`() {
        val code = """
            import androidx.compose.runtime.Composable
            import androidx.wear.tiles.tooling.preview.Preview

            @Preview
            @Composable
            fun MyPreview() {}
        """.trimIndent()
        val tiles = """
            package androidx.wear.tiles.tooling.preview

            annotation class Preview(val name: String = "")

            class TilePreviewData
        """.trimIndent()
        val runtime = """
            package androidx.compose.runtime

            annotation class Composable
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tiles, runtime)

        assertEquals(0, findings.size)
    }
}

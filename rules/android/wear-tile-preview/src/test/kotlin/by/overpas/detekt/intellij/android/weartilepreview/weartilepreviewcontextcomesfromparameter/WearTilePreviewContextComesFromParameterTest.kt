package by.overpas.detekt.intellij.android.weartilepreview.weartilepreviewcontextcomesfromparameter

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class WearTilePreviewContextComesFromParameterTest {

    private val environment = createEnvironment()

    private val sut = WearTilePreviewContextComesFromParameter(Config.empty)

    @Test
    fun `a foreign context argument in a tile preview is reported`() {
        val code = """
            import android.app.Activity
            import android.content.Context
            import androidx.wear.tiles.tooling.preview.Preview
            import androidx.wear.tiles.tooling.preview.TilePreviewData

            private val someOtherContext = Activity()

            fun tile(context: Context) = TilePreviewData()

            @Preview
            fun MyPreview() = tile(someOtherContext)
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
        val activity = """
            package android.app

            import android.content.Context

            open class Activity : Context()
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tiles, context, activity)
        val reported = findings.single().entity.location.text
            .run { code.substring(start, end) }

        assertEquals("someOtherContext", reported)
    }

    @Test
    fun `a foreign context argument in a tile preview with a context parameter is reported`() {
        val code = """
            import android.app.Activity
            import android.content.Context
            import androidx.wear.tiles.tooling.preview.Preview
            import androidx.wear.tiles.tooling.preview.TilePreviewData

            private val someOtherContext = Activity()

            fun tile(context: Context) = TilePreviewData()

            @Preview
            fun MyPreview(context: Context) = tile(someOtherContext)
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
        val activity = """
            package android.app

            import android.content.Context

            open class Activity : Context()
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tiles, context, activity)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a nullable foreign context argument is reported`() {
        val code = """
            import android.content.Context
            import androidx.wear.tiles.tooling.preview.Preview
            import androidx.wear.tiles.tooling.preview.TilePreviewData

            private val someOtherContext: Context? = null

            fun tile(context: Context?) = TilePreviewData()

            @Preview
            fun MyPreview() = tile(someOtherContext)
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
    fun `a foreign context receiver of an android call is reported`() {
        val code = """
            import android.content.Context
            import androidx.wear.tiles.tooling.preview.Preview
            import androidx.wear.tiles.tooling.preview.TilePreviewData

            private val someOtherContext = Context()

            @Preview
            fun MyPreview(): TilePreviewData {
                val invalid = someOtherContext.isUiContext
                return TilePreviewData()
            }
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
        val reported = findings.single().entity.location.text
            .run { code.substring(start, end) }

        assertEquals("someOtherContext", reported)
    }

    @Test
    fun `a foreign context receiver of a user declared call passes`() {
        val code = """
            import android.content.Context
            import androidx.wear.tiles.tooling.preview.Preview
            import androidx.wear.tiles.tooling.preview.TilePreviewData

            class MyContext : Context() {
                fun myOwnTileFunction() = TilePreviewData()
            }

            private val someOtherContext = MyContext()

            @Preview
            fun MyPreview() = someOtherContext.myOwnTileFunction()
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
    fun `the context parameter passes`() {
        val code = """
            import android.content.Context
            import androidx.wear.tiles.tooling.preview.Preview
            import androidx.wear.tiles.tooling.preview.TilePreviewData

            fun tile(context: Context) = TilePreviewData()

            @Preview
            fun MyPreview(context: Context): TilePreviewData {
                context.resources
                context.getString(1)
                return tile(context)
            }
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
    fun `an enclosing context class used as this passes`() {
        val code = """
            import android.app.Activity
            import android.content.Context
            import androidx.wear.tiles.tooling.preview.Preview
            import androidx.wear.tiles.tooling.preview.TilePreviewData

            fun tile(context: Context) = TilePreviewData()

            class MyContext : Activity() {
                @Preview
                fun MyPreview() = tile(this)
            }
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
        val activity = """
            package android.app

            import android.content.Context

            open class Activity : Context()
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tiles, context, activity)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a local copy of the context parameter is reported`() {
        val code = """
            import android.content.Context
            import androidx.wear.tiles.tooling.preview.Preview
            import androidx.wear.tiles.tooling.preview.TilePreviewData

            fun tile(context: Context) = TilePreviewData()

            @Preview
            fun MyPreview(context: Context): TilePreviewData {
                val copy = context
                return tile(copy)
            }
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
    fun `a foreign context in a function without a tile preview annotation passes`() {
        val code = """
            import android.content.Context
            import androidx.wear.tiles.tooling.preview.TilePreviewData

            private val someOtherContext = Context()

            fun tile(context: Context) = TilePreviewData()

            fun MyPreview() = tile(someOtherContext)
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
}

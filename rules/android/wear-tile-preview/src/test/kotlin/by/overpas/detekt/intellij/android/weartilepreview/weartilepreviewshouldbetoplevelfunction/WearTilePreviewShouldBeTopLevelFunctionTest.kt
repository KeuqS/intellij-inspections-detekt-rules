package by.overpas.detekt.intellij.android.weartilepreview.weartilepreviewshouldbetoplevelfunction

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class WearTilePreviewShouldBeTopLevelFunctionTest {

    private val environment = createEnvironment()

    private val sut = WearTilePreviewShouldBeTopLevelFunction(Config.empty)

    @Test
    fun `local tile preview functions are reported`() {
        val code = """
            import androidx.wear.tiles.tooling.preview.Preview
            import androidx.wear.tiles.tooling.preview.TilePreviewData

            @Preview(name = "top level preview")
            fun topLevelPreview(): TilePreviewData {
                @Preview(name = "not a top level preview")
                fun notTopLevelFunctionPreview(): TilePreviewData {
                    @Preview(name = "not a top level preview, with a lot of nesting")
                    fun superNestedPreview() = TilePreviewData()
                    fun validSuperNestedMethod() = TilePreviewData()
                    return TilePreviewData()
                }
                fun validNotTopLevelMethod() = TilePreviewData()
                return TilePreviewData()
            }
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
    fun `a local tile preview function in a class method is reported`() {
        val code = """
            import androidx.wear.tiles.tooling.preview.Preview
            import androidx.wear.tiles.tooling.preview.TilePreviewData

            class SomeClass {
                @Preview
                fun classMethodPreview(): TilePreviewData {
                    @Preview
                    fun localPreview() = TilePreviewData()
                    return TilePreviewData()
                }
            }
        """.trimIndent()
        val tiles = """
            package androidx.wear.tiles.tooling.preview

            annotation class Preview(val name: String = "")

            class TilePreviewData
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, tiles)
        val reported = findings.single().entity.location.text
            .run { code.substring(start, end) }

        assertEquals("@Preview\n        fun localPreview() = TilePreviewData()", reported)
    }

    @Test
    fun `tile previews in a top level class with a default constructor pass`() {
        val code = """
            import androidx.wear.tiles.tooling.preview.Preview
            import androidx.wear.tiles.tooling.preview.TilePreviewData

            class SomeClass {
                @Preview
                private fun privateClassMethodPreview() = TilePreviewData()
            }

            class DefaultValueClass(val size: Int = 1) {
                @Preview
                fun classMethodPreview() = TilePreviewData()
            }

            class SecondaryConstructorClass(val size: Int) {
                constructor() : this(1)

                @Preview
                fun classMethodPreview() = TilePreviewData()
            }

            object SomeObject {
                @Preview
                fun objectMethodPreview() = TilePreviewData()
            }
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
    fun `a tile preview in a nested class is reported`() {
        val code = """
            import androidx.wear.tiles.tooling.preview.Preview
            import androidx.wear.tiles.tooling.preview.TilePreviewData

            private class PrivateClass {
                class NotTopLevelClass {
                    @Preview("in a non top level class")
                    fun classMethodPreview() = TilePreviewData()
                    fun validClassMethod() = TilePreviewData()
                }

                @Preview
                fun classMethodPreview() = TilePreviewData()
            }
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
    fun `a tile preview in a companion object is reported`() {
        val code = """
            import androidx.wear.tiles.tooling.preview.Preview
            import androidx.wear.tiles.tooling.preview.TilePreviewData

            class SomeClass {
                companion object {
                    @Preview
                    fun companionPreview() = TilePreviewData()
                }
            }
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
    fun `a tile preview in a class without a default constructor is reported`() {
        val code = """
            import androidx.wear.tiles.tooling.preview.Preview
            import androidx.wear.tiles.tooling.preview.TilePreviewData

            class SomeClassWithoutDefaultConstructor(i: Int) {
                @Preview("in a class with parameters")
                fun classMethodPreview() = TilePreviewData()
                fun validClassMethod() = TilePreviewData()
            }
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
    fun `a tile preview in an object literal is reported`() {
        val code = """
            import androidx.wear.tiles.tooling.preview.Preview
            import androidx.wear.tiles.tooling.preview.TilePreviewData

            val previews = object {
                @Preview
                fun objectPreview() = TilePreviewData()
            }
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
    fun `a misplaced function with a tile multipreview annotation is reported`() {
        val code = """
            import androidx.wear.tiles.tooling.preview.Preview
            import androidx.wear.tiles.tooling.preview.TilePreviewData

            @Preview
            annotation class TileMultiPreview

            class SomeClassWithoutDefaultConstructor(i: Int) {
                @TileMultiPreview
                fun classMethodPreview() = TilePreviewData()
            }
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
    fun `a misplaced function with a compose preview annotation passes`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            class SomeClassWithoutDefaultConstructor(i: Int) {
                @Preview
                fun classMethodPreview() {}
            }
        """.trimIndent()
        val compose = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, compose)

        assertEquals(0, findings.size)
    }
}

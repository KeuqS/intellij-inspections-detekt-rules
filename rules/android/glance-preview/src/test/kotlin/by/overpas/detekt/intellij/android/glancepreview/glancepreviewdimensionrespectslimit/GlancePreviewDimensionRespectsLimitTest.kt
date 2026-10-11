package by.overpas.detekt.intellij.android.glancepreview.glancepreviewdimensionrespectslimit

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class GlancePreviewDimensionRespectsLimitTest {

    private val environment = createEnvironment()

    private val sut = GlancePreviewDimensionRespectsLimit(Config.empty)

    @Test
    fun `a width above the limit on a function is reported`() {
        val code = """
            import androidx.glance.preview.Preview

            @Preview(widthDp = 3001)
            fun MyPreview() {}
        """.trimIndent()
        val preview = """
            package androidx.glance.preview

            annotation class Preview(val widthDp: Int = -1, val heightDp: Int = -1)
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a height below the limit is reported`() {
        val code = """
            import androidx.glance.preview.Preview

            @Preview(heightDp = 0)
            fun MyPreview() {}
        """.trimIndent()
        val preview = """
            package androidx.glance.preview

            annotation class Preview(val widthDp: Int = -1, val heightDp: Int = -1)
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a constant above the limit on an annotation class is reported`() {
        val code = """
            import androidx.glance.preview.Preview

            private const val BAD_WIDTH = 3001

            @Preview(widthDp = BAD_WIDTH)
            annotation class BadPreview
        """.trimIndent()
        val preview = """
            package androidx.glance.preview

            annotation class Preview(val widthDp: Int = -1, val heightDp: Int = -1)
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(1, findings.size)
    }

    @Test
    fun `width and height above the limit in one annotation are reported once on the height`() {
        val code = """
            import androidx.glance.preview.Preview

            @Preview(heightDp = 3001, widthDp = 3002)
            fun MyPreview() {}
        """.trimIndent()
        val preview = """
            package androidx.glance.preview

            annotation class Preview(val widthDp: Int = -1, val heightDp: Int = -1)
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)
        val reported = findings.single().entity.location.text
            .run { code.substring(start, end) }

        assertEquals("heightDp = 3001", reported)
    }

    @Test
    fun `dimensions within the limits pass`() {
        val code = """
            import androidx.glance.preview.Preview

            private const val GOOD_WIDTH = 2000

            @Preview(widthDp = GOOD_WIDTH, heightDp = 1)
            fun MyPreview() {}

            @Preview(widthDp = 3000)
            annotation class GoodPreview
        """.trimIndent()
        val preview = """
            package androidx.glance.preview

            annotation class Preview(val widthDp: Int = -1, val heightDp: Int = -1)
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(0, findings.size)
    }

    @Test
    fun `parameters of a multipreview annotation pass`() {
        val code = """
            import androidx.glance.preview.Preview

            @Preview(widthDp = 2000)
            annotation class GoodPreview(val widthDp: Int = 3001)

            @GoodPreview(widthDp = 5000)
            fun MyPreview() {}
        """.trimIndent()
        val preview = """
            package androidx.glance.preview

            annotation class Preview(val widthDp: Int = -1, val heightDp: Int = -1)
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a non constant dimension passes`() {
        val code = """
            import androidx.glance.preview.Preview

            val width = 5000

            @Preview(widthDp = width)
            fun MyPreview() {}
        """.trimIndent()
        val preview = """
            package androidx.glance.preview

            annotation class Preview(val widthDp: Int = -1, val heightDp: Int = -1)
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a compose preview with a large width passes`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            @Preview(widthDp = 5000)
            fun MyPreview() {}
        """.trimIndent()
        val preview = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview(val widthDp: Int = -1)
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(0, findings.size)
    }
}

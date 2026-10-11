package by.overpas.detekt.intellij.android.composepreview.previewdeviceshouldusenewspec

import dev.detekt.api.Config
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class PreviewDeviceShouldUseNewSpecTest {

    private val environment = createEnvironment()

    private val sut = PreviewDeviceShouldUseNewSpec(Config.empty)

    @Test
    fun `a legacy devices constant is reported on the argument`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Devices
            import androidx.compose.ui.tooling.preview.Preview

            @Preview(name = "Phone", device = Devices.PIXEL_4)
            fun MyPreview() {}
        """.trimIndent()
        val preview = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview(val name: String = "", val device: String = "")

            object Devices {
                const val PIXEL_4 = "spec:id=reference_phone,shape=Normal,width=411,height=891,unit=dp,dpi=420"
            }
        """.trimIndent()
        val expected = listOf("device = Devices.PIXEL_4")

        val findings = sut.lintWithContext(environment, code, preview)
        val reportedTexts = findings.map { finding ->
            finding.entity.location.text.run { code.substring(start, end) }
        }

        assertEquals(expected, reportedTexts)
    }

    @Test
    fun `a legacy spec in a local constant on an annotation class is reported`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            const val PHONE = "spec:id=reference_phone,shape=Normal,width=411,height=891,unit=dp,dpi=420"

            @Preview(device = PHONE)
            annotation class PhonePreview
        """.trimIndent()
        val preview = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview(val name: String = "", val device: String = "")
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(1, findings.size)
    }

    @Test
    fun `a new device spec passes`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            const val FOLDABLE = "spec:width=673dp,height=841dp"

            @Preview(device = FOLDABLE)
            fun MyPreview() {}
        """.trimIndent()
        val preview = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview(val name: String = "", val device: String = "")
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a device id passes`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            @Preview(device = "id:pixel_4")
            fun MyPreview() {}
        """.trimIndent()
        val preview = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview(val name: String = "", val device: String = "")
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(0, findings.size)
    }

    @Test
    fun `a multipreview annotation parameter with a legacy spec passes`() {
        val code = """
            import androidx.compose.ui.tooling.preview.Preview

            @Preview
            annotation class DevicePreview(val device: String = "")

            @DevicePreview(device = "spec:id=reference_phone,shape=Normal,width=411,height=891,unit=dp,dpi=420")
            fun MyPreview() {}
        """.trimIndent()
        val preview = """
            package androidx.compose.ui.tooling.preview

            annotation class Preview(val name: String = "", val device: String = "")
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code, preview)

        assertEquals(0, findings.size)
    }
}

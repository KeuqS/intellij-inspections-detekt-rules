package by.overpas.detekt.intellij.android.weartilepreview

import kotlin.test.Test
import kotlin.test.assertEquals

class WearTilePreviewRuleSetProviderTest {

    private val sut = WearTilePreviewRuleSetProvider()

    @Test
    fun `rule set has the intellij-android-wear-tile-preview id`() {
        val ruleSet = sut.instance()

        assertEquals("intellij-android-wear-tile-preview", ruleSet.id.value)
    }

    @Test
    fun `default config lists every rule of the rule set`() {
        val ruleEntry = Regex("^ {2}(\\w+):$")
        val configured = checkNotNull(javaClass.getResource("/config/config.yml")).readText()
            .lines()
            .mapNotNull { line -> ruleEntry.find(line)?.run { groupValues[1] } }
            .toSet()

        val ruleNames = sut.instance().rules.keys
            .map { it.value }
            .toSet()

        assertEquals(configured, ruleNames)
    }
}

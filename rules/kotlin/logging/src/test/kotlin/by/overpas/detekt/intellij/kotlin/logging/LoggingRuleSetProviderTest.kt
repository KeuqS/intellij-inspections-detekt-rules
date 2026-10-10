package by.overpas.detekt.intellij.kotlin.logging

import kotlin.test.Test
import kotlin.test.assertEquals

class LoggingRuleSetProviderTest {

    private val sut = LoggingRuleSetProvider()

    @Test
    fun `rule set has the intellij-kotlin-logging id`() {
        val ruleSet = sut.instance()

        assertEquals("intellij-kotlin-logging", ruleSet.id.value)
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

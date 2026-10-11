package by.overpas.detekt.intellij.gradle.bestpractices

import kotlin.test.Test
import kotlin.test.assertEquals

class BestPracticesRuleSetProviderTest {

    private val sut = BestPracticesRuleSetProvider()

    @Test
    fun `rule set has the intellij-gradle-best-practices id`() {
        val ruleSet = sut.instance()

        assertEquals("intellij-gradle-best-practices", ruleSet.id.value)
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

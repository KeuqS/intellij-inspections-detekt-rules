package by.overpas.detekt.intellij.kotlin.codemigration

import kotlin.test.Test
import kotlin.test.assertEquals

class CodeMigrationRuleSetProviderTest {

    private val sut = CodeMigrationRuleSetProvider()

    @Test
    fun `rule set has the intellij-kotlin-code-migration id`() {
        val ruleSet = sut.instance()

        assertEquals("intellij-kotlin-code-migration", ruleSet.id.value)
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

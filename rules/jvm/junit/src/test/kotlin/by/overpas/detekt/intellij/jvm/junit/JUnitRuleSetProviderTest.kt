package by.overpas.detekt.intellij.jvm.junit

import kotlin.test.Test
import kotlin.test.assertEquals

class JUnitRuleSetProviderTest {

    private val sut = JUnitRuleSetProvider()

    @Test
    fun `rule set has the intellij-jvm-junit id`() {
        val ruleSet = sut.instance()

        assertEquals("intellij-jvm-junit", ruleSet.id.value)
    }
}

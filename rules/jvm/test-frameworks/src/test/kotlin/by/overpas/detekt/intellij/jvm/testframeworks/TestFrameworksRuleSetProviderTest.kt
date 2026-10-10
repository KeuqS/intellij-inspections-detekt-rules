package by.overpas.detekt.intellij.jvm.testframeworks

import kotlin.test.Test
import kotlin.test.assertEquals

class TestFrameworksRuleSetProviderTest {

    private val sut = TestFrameworksRuleSetProvider()

    @Test
    fun `rule set has the intellij-jvm-test-frameworks id`() {
        val ruleSet = sut.instance()

        assertEquals("intellij-jvm-test-frameworks", ruleSet.id.value)
    }
}

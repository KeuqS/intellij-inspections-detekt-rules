package by.overpas.detekt.intellij.jvm.junit

import by.overpas.detekt.intellij.IntellijInspectionGroup
import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider

@IntellijInspectionGroup("junit.inspections.group.name")
class JUnitRuleSetProvider : RuleSetProvider {

    override val ruleSetId = RuleSetId("intellij-jvm-junit")

    override fun instance() =
        RuleSet(
            ruleSetId,
            listOf(
                ::AssertEqualsCalledOnArray,
                ::AssertEqualsMayBeAssertSame,
                ::IgnoredJUnitTest,
                ::JUnit3StyleTestMethodInJUnit4Class,
                ::JUnit5AssertionsConverter,
                ::JUnit5Converter,
                ::JUnitMixedFramework,
                ::Junit4Converter,
                ::Junit4RunWithInspection,
                ::MigrateAssertToMatcherAssert,
                ::ParameterizedParametersStaticCollection,
                ::SuperTearDownInFinally,
                ::UseOfObsoleteAssert,
            ),
        )
}

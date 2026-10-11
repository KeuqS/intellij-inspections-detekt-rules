package by.overpas.detekt.intellij.jvm.junit

import by.overpas.detekt.intellij.IntellijInspectionGroup
import by.overpas.detekt.intellij.jvm.junit.assertequalscalledonarray.AssertEqualsCalledOnArray
import by.overpas.detekt.intellij.jvm.junit.assertequalsmaybeassertsame.AssertEqualsMayBeAssertSame
import by.overpas.detekt.intellij.jvm.junit.ignoredjunittest.IgnoredJUnitTest
import by.overpas.detekt.intellij.jvm.junit.junit3styletestmethodinjunit4class.JUnit3StyleTestMethodInJUnit4Class
import by.overpas.detekt.intellij.jvm.junit.junit4runwithinspection.Junit4RunWithInspection
import by.overpas.detekt.intellij.jvm.junit.junit5assertionsconverter.JUnit5AssertionsConverter
import by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration.JUnitMalformedDeclaration
import by.overpas.detekt.intellij.jvm.junit.junitmixedframework.JUnitMixedFramework
import by.overpas.detekt.intellij.jvm.junit.migrateasserttomatcherassert.MigrateAssertToMatcherAssert
import by.overpas.detekt.intellij.jvm.junit.parameterizedparametersstaticcollection.ParameterizedParametersStaticCollection
import by.overpas.detekt.intellij.jvm.junit.superteardowninfinally.SuperTearDownInFinally
import by.overpas.detekt.intellij.jvm.junit.useofobsoleteassert.UseOfObsoleteAssert
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
                ::JUnitMalformedDeclaration,
                ::JUnitMixedFramework,
                ::Junit4RunWithInspection,
                ::MigrateAssertToMatcherAssert,
                ::ParameterizedParametersStaticCollection,
                ::SuperTearDownInFinally,
                ::UseOfObsoleteAssert,
            ),
        )
}

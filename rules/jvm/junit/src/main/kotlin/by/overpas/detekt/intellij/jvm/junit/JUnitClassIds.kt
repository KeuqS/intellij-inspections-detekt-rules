package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

internal val JUNIT_FRAMEWORK_TEST_CASE = classId("junit.framework.TestCase")
internal val JUNIT_FRAMEWORK_ASSERT = classId("junit.framework.Assert")
internal val ORG_JUNIT_ASSERT = classId("org.junit.Assert")
internal val ORG_JUNIT_TEST = classId("org.junit.Test")
internal val ORG_JUNIT_IGNORE = classId("org.junit.Ignore")
internal val ORG_JUNIT_BEFORE = classId("org.junit.Before")
internal val ORG_JUNIT_AFTER = classId("org.junit.After")
internal val ORG_JUNIT_BEFORE_CLASS = classId("org.junit.BeforeClass")
internal val ORG_JUNIT_AFTER_CLASS = classId("org.junit.AfterClass")
internal val ORG_JUNIT_RULE = classId("org.junit.Rule")
internal val ORG_JUNIT_CLASS_RULE = classId("org.junit.ClassRule")
internal val ORG_JUNIT_RUN_WITH = classId("org.junit.runner.RunWith")
internal val JUPITER_ASSERTIONS = classId("org.junit.jupiter.api.Assertions")
internal val JUPITER_TEST = classId("org.junit.jupiter.api.Test")
internal val JUPITER_DISABLED = classId("org.junit.jupiter.api.Disabled")
internal val JUPITER_BEFORE_EACH = classId("org.junit.jupiter.api.BeforeEach")
internal val JUPITER_AFTER_EACH = classId("org.junit.jupiter.api.AfterEach")
internal val JUPITER_BEFORE_ALL = classId("org.junit.jupiter.api.BeforeAll")
internal val JUPITER_AFTER_ALL = classId("org.junit.jupiter.api.AfterAll")
internal val JUPITER_TEST_FACTORY = classId("org.junit.jupiter.api.TestFactory")
internal val JUPITER_REPEATED_TEST = classId("org.junit.jupiter.api.RepeatedTest")
internal val JUPITER_PARAMETERIZED_TEST = classId("org.junit.jupiter.params.ParameterizedTest")

internal fun classId(fqName: String): ClassId = ClassId.topLevel(FqName(fqName))

internal val ORG_JUNIT_ASSUME = classId("org.junit.Assume")
internal val ORG_JUNIT_PARAMETERIZED = classId("org.junit.runners.Parameterized")
internal val ORG_JUNIT_PARAMETERS = ORG_JUNIT_PARAMETERIZED.createNestedClassId(Name.identifier("Parameters"))
internal val TESTNG_ASSERT = classId("org.testng.Assert")
internal val TESTNG_ASSERT_JUNIT = classId("org.testng.AssertJUnit")
internal val JUPITER_ASSUMPTIONS = classId("org.junit.jupiter.api.Assumptions")
internal val JUPITER_TEST_TEMPLATE = classId("org.junit.jupiter.api.TestTemplate")
internal val JUNIT_PLATFORM_TESTABLE = classId("org.junit.platform.commons.annotation.Testable")
internal val HAMCREST_MATCHERS = classId("org.hamcrest.Matchers")
internal val HAMCREST_CORE_MATCHERS = classId("org.hamcrest.CoreMatchers")
internal val HAMCREST_ORDERING_COMPARISON = classId("org.hamcrest.number.OrderingComparison")
internal val JAVA_LANG_OBJECT = classId("java.lang.Object")

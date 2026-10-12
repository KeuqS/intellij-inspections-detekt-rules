package by.overpas.detekt.intellij.jvm.testframeworks

import org.jetbrains.kotlin.name.ClassId

internal val JUNIT_FRAMEWORK_TEST_CASE = ClassId.fromString("junit/framework/TestCase")
internal val JUNIT_FRAMEWORK_TEST_SUITE = ClassId.fromString("junit/framework/TestSuite")
internal val ORG_JUNIT_TEST = ClassId.fromString("org/junit/Test")
internal val ORG_JUNIT_IGNORE = ClassId.fromString("org/junit/Ignore")
internal val ORG_JUNIT_RUN_WITH = ClassId.fromString("org/junit/runner/RunWith")
internal val ORG_JUNIT_PARAMETERIZED = ClassId.fromString("org/junit/runners/Parameterized")
internal val JUPITER_NESTED = ClassId.fromString("org/junit/jupiter/api/Nested")
internal val TESTNG_TEST = ClassId.fromString("org/testng/annotations/Test")
internal val KOTLIN_THROWS = ClassId.fromString("kotlin/Throws")
internal val KOTLIN_JVM_THROWS = ClassId.fromString("kotlin/jvm/Throws")
internal val ASSERTJ_ASSERT = ClassId.fromString("org/assertj/core/api/Assert")
internal val ASSERTJ_ASSERTIONS = ClassId.fromString("org/assertj/core/api/Assertions")

internal val JUPITER_TEST_ANNOTATIONS = setOf(
    ClassId.fromString("org/junit/jupiter/api/Test"),
    ClassId.fromString("org/junit/jupiter/api/RepeatedTest"),
    ClassId.fromString("org/junit/jupiter/api/TestFactory"),
    ClassId.fromString("org/junit/jupiter/api/TestTemplate"),
    ClassId.fromString("org/junit/jupiter/params/ParameterizedTest"),
)

internal val LIFECYCLE_ANNOTATIONS = setOf(
    ClassId.fromString("org/junit/After"),
    ClassId.fromString("org/junit/AfterClass"),
    ClassId.fromString("org/junit/Before"),
    ClassId.fromString("org/junit/BeforeClass"),
    ClassId.fromString("org/junit/jupiter/api/AfterAll"),
    ClassId.fromString("org/junit/jupiter/api/AfterEach"),
    ClassId.fromString("org/junit/jupiter/api/BeforeAll"),
    ClassId.fromString("org/junit/jupiter/api/BeforeEach"),
    ClassId.fromString("org/testng/annotations/AfterClass"),
    ClassId.fromString("org/testng/annotations/AfterMethod"),
    ClassId.fromString("org/testng/annotations/BeforeClass"),
    ClassId.fromString("org/testng/annotations/BeforeMethod"),
)

internal val ASSERT_OWNERS = setOf(
    ClassId.fromString("junit/framework/Assert"),
    JUNIT_FRAMEWORK_TEST_CASE,
    ClassId.fromString("org/junit/Assert"),
    ClassId.fromString("org/junit/jupiter/api/Assertions"),
    ClassId.fromString("org/testng/Assert"),
    ClassId.fromString("org/testng/AssertJUnit"),
)

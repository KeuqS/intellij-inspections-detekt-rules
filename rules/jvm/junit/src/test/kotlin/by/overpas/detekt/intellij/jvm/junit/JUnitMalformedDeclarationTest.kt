package by.overpas.detekt.intellij.jvm.junit

import dev.detekt.api.Config
import dev.detekt.test.TestConfig
import dev.detekt.test.lintWithContext
import dev.detekt.test.utils.createEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

class JUnitMalformedDeclarationTest {

    private val environment = createEnvironment()

    private val sut = JUnitMalformedDeclaration(Config.empty)

    @Test
    fun `a junit 4 test method with a result and a parameter is reported`() {
        val code = """
            import org.junit.Test

            class FooTest {
                @Test
                fun test(i: Int): String = "a"
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Method 'test' annotated with '@Test' should be of type 'Unit' and not declare parameter 'i'"), findings.map { it.message })
    }

    @Test
    fun `a private junit 4 test method is reported`() {
        val code = """
            import org.junit.Test

            class FooTest {
                @Test
                private fun test() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Method 'test' annotated with '@Test' should be public"), findings.map { it.message })
    }

    @Test
    fun `a well formed junit 4 test method passes`() {
        val code = """
            import org.junit.Test

            class FooTest {
                @Test
                fun test() {}

                @Test
                fun withTempDir(@org.junit.jupiter.api.io.TempDir dir: java.io.File) {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(emptyList(), findings.map { it.message })
    }

    @Test
    fun `a static junit 4 test method in a companion object is reported`() {
        val code = """
            import org.junit.Test

            class FooTest {
                companion object {
                    @JvmStatic
                    @Test
                    fun test() {}
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(
            listOf(
                "Method 'test' annotated with '@Test' should be non-static",
                "Test class 'Companion' is not constructable because it should have exactly one 'public' no-arg constructor",
            ),
            findings.map { it.message },
        )
    }

    @Test
    fun `a junit 4 test method under a custom runner passes`() {
        val code = """
            import org.junit.Test
            import org.junit.runner.RunWith

            class MyRunner(type: Class<*>) : org.junit.runners.BlockJUnit4ClassRunner(type)

            @RunWith(MyRunner::class)
            class FooTest {
                @Test
                fun test(i: Int): Int = i
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(emptyList(), findings.map { it.message })
    }

    @Test
    fun `a junit 4 test method under a known runner is reported`() {
        val code = """
            import org.junit.Test
            import org.junit.runner.RunWith

            @RunWith(org.junit.runners.JUnit4::class)
            class FooTest {
                @Test
                fun test(i: Int) {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Method 'test' annotated with '@Test' should not declare parameter 'i'"), findings.map { it.message })
    }

    @Test
    fun `a junit 4 before method with a result and a parameter is reported`() {
        val code = """
            import org.junit.Before

            class FooTest {
                @Before
                fun before(i: Int): String = i.toString()
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Method 'before' annotated with '@Before' should be of type 'Unit' and not declare parameter 'i'"), findings.map { it.message })
    }

    @Test
    fun `a non-static before class method is reported`() {
        val code = """
            import org.junit.BeforeClass

            class FooTest {
                @BeforeClass
                fun beforeClass() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Method 'beforeClass' annotated with '@BeforeClass' should be static"), findings.map { it.message })
    }

    @Test
    fun `a private before class method with parameters is reported`() {
        val code = """
            import org.junit.BeforeClass

            class FooTest {
                companion object {
                    @JvmStatic
                    @BeforeClass
                    private fun beforeClass(a: Int, b: Int, c: Int) {}
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Method 'beforeClass' annotated with '@BeforeClass' should be public and not declare parameters 'a', 'b' and 'c'"), findings.map { it.message })
    }

    @Test
    fun `a static before class method passes`() {
        val code = """
            import org.junit.AfterClass

            class FooTest {
                companion object {
                    @JvmStatic
                    @AfterClass
                    fun afterClass() {}
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(emptyList(), findings.map { it.message })
    }

    @Test
    fun `a non-static before all method is reported`() {
        val code = """
            import org.junit.jupiter.api.BeforeAll

            class FooTest {
                @BeforeAll
                fun beforeAll() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Method 'beforeAll' annotated with '@BeforeAll' should be static"), findings.map { it.message })
    }

    @Test
    fun `a non-static before all method in a per class test passes`() {
        val code = """
            import org.junit.jupiter.api.BeforeAll
            import org.junit.jupiter.api.TestInstance

            @TestInstance(TestInstance.Lifecycle.PER_CLASS)
            class FooTest {
                @BeforeAll
                fun beforeAll() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(emptyList(), findings.map { it.message })
    }

    @Test
    fun `a before all method with two parameters is reported`() {
        val code = """
            import org.junit.jupiter.api.AfterAll

            class FooTest {
                companion object {
                    @JvmStatic
                    @AfterAll
                    fun afterAll(a: Int, info: org.junit.jupiter.api.TestInfo, b: String): Int = a
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Method 'afterAll' annotated with '@AfterAll' should be of type 'Unit' and not declare parameters 'a' and 'b'"), findings.map { it.message })
    }

    @Test
    fun `a before each method with an unknown parameter is reported`() {
        val code = """
            import org.junit.jupiter.api.BeforeEach

            class FooTest {
                @BeforeEach
                private fun beforeEach(i: Int) {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Method 'beforeEach' annotated with '@BeforeEach' should be public and not declare parameter 'i'"), findings.map { it.message })
    }

    @Test
    fun `a before each method with injected parameters passes`() {
        val code = """
            import org.junit.jupiter.api.AfterEach

            class FooTest {
                @AfterEach
                fun afterEach(
                    info: org.junit.jupiter.api.TestInfo,
                    repetition: org.junit.jupiter.api.RepetitionInfo,
                    reporter: org.junit.jupiter.api.TestReporter,
                ) {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(emptyList(), findings.map { it.message })
    }

    @Test
    fun `a before each method with a parameter resolver extension passes`() {
        val code = """
            class Resolver : org.junit.jupiter.api.extension.ParameterResolver {
                override fun supportsParameter(
                    parameterContext: org.junit.jupiter.api.extension.ParameterContext,
                    extensionContext: org.junit.jupiter.api.extension.ExtensionContext,
                ) = true

                override fun resolveParameter(
                    parameterContext: org.junit.jupiter.api.extension.ParameterContext,
                    extensionContext: org.junit.jupiter.api.extension.ExtensionContext,
                ): Any = ""
            }

            @org.junit.jupiter.api.extension.ExtendWith(Resolver::class)
            annotation class WithResolver

            @WithResolver
            class FooTest {
                @org.junit.jupiter.api.BeforeEach
                fun beforeEach(value: String) {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(emptyList(), findings.map { it.message })
    }

    @Test
    fun `a before each method with a parameter resolver in extensions passes`() {
        val code = """
            class Resolver : org.junit.jupiter.api.extension.ParameterResolver {
                override fun supportsParameter(
                    parameterContext: org.junit.jupiter.api.extension.ParameterContext,
                    extensionContext: org.junit.jupiter.api.extension.ExtensionContext,
                ) = true

                override fun resolveParameter(
                    parameterContext: org.junit.jupiter.api.extension.ParameterContext,
                    extensionContext: org.junit.jupiter.api.extension.ExtensionContext,
                ): Any = ""
            }

            @org.junit.jupiter.api.extension.Extensions(
                org.junit.jupiter.api.extension.ExtendWith(Resolver::class),
            )
            class FooTest {
                @org.junit.jupiter.api.BeforeEach
                fun beforeEach(value: String) {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(emptyList(), findings.map { it.message })
    }

    @Test
    fun `a junit 5 test method with an unknown parameter is reported`() {
        val code = """
            import org.junit.jupiter.api.Test

            class FooTest {
                @Test
                fun test(value: String) {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Method 'test' annotated with '@Test' should not declare parameter 'value'"), findings.map { it.message })
    }

    @Test
    fun `a junit 5 test method with injected parameters passes`() {
        val code = """
            class Resolver : org.junit.jupiter.api.extension.ParameterResolver {
                override fun supportsParameter(
                    parameterContext: org.junit.jupiter.api.extension.ParameterContext,
                    extensionContext: org.junit.jupiter.api.extension.ExtensionContext,
                ) = true

                override fun resolveParameter(
                    parameterContext: org.junit.jupiter.api.extension.ParameterContext,
                    extensionContext: org.junit.jupiter.api.extension.ExtensionContext,
                ): Any = ""
            }

            @Target(AnnotationTarget.VALUE_PARAMETER)
            @org.junit.jupiter.api.extension.ExtendWith(Resolver::class)
            annotation class Resolved

            class FooTest {
                @org.junit.jupiter.api.Test
                fun test(
                    info: org.junit.jupiter.api.TestInfo,
                    reporter: org.junit.jupiter.api.TestReporter,
                    @org.junit.jupiter.api.io.TempDir dir: java.io.File,
                    @Resolved value: String,
                ) {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(emptyList(), findings.map { it.message })
    }

    @Test
    fun `a junit 5 test method with a registered parameter resolver passes`() {
        val code = """
            class Resolver : org.junit.jupiter.api.extension.ParameterResolver {
                override fun supportsParameter(
                    parameterContext: org.junit.jupiter.api.extension.ParameterContext,
                    extensionContext: org.junit.jupiter.api.extension.ExtensionContext,
                ) = true

                override fun resolveParameter(
                    parameterContext: org.junit.jupiter.api.extension.ParameterContext,
                    extensionContext: org.junit.jupiter.api.extension.ExtensionContext,
                ): Any = ""
            }

            class FooTest {
                @JvmField
                @org.junit.jupiter.api.extension.RegisterExtension
                val resolver = Resolver()

                @org.junit.jupiter.api.Test
                fun test(value: String) {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(emptyList(), findings.map { it.message })
    }

    @Test
    fun `a suspend junit 5 test method is reported`() {
        val code = """
            import org.junit.jupiter.api.Test

            class FooTest {
                @Test
                suspend fun test() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Method 'test' annotated with '@Test' should not be a suspending function"), findings.map { it.message })
    }

    @Test
    fun `a suspend junit 5 test method passes when suspend functions are allowed`() {
        val code = """
            import org.junit.jupiter.api.Test

            class FooTest {
                @Test
                suspend fun test() {}
            }
        """.trimIndent()
        val rule = JUnitMalformedDeclaration(TestConfig("areSuspendFunctionsAllowed" to true))

        val findings = rule.lintWithContext(environment, code)

        assertEquals(emptyList(), findings.map { it.message })
    }

    @Test
    fun `a junit 4 rule property without a public field is reported`() {
        val code = """
            class SomeTestRule : org.junit.rules.TestRule {
                override fun apply(base: org.junit.runners.model.Statement, description: org.junit.runner.Description) = base
            }

            class FooTest {
                @org.junit.Rule
                var rule = SomeTestRule()
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Field 'rule' annotated with '@Rule' should be public"), findings.map { it.message })
    }

    @Test
    fun `a junit 4 rule with a public field or getter passes`() {
        val code = """
            class SomeTestRule : org.junit.rules.TestRule {
                override fun apply(base: org.junit.runners.model.Statement, description: org.junit.runner.Description) = base
            }

            class FooTest {
                @JvmField
                @org.junit.Rule
                val rule = SomeTestRule()

                @get:org.junit.Rule
                val getterRule = SomeTestRule()

                @org.junit.Rule
                lateinit var lateRule: SomeTestRule
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(emptyList(), findings.map { it.message })
    }

    @Test
    fun `a private junit 4 rule in an object is reported`() {
        val code = """
            class SomeTestRule : org.junit.rules.TestRule {
                override fun apply(base: org.junit.runners.model.Statement, description: org.junit.runner.Description) = base
            }

            object FooTest {
                @org.junit.Rule
                private var rule = SomeTestRule()
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Field 'rule' annotated with '@Rule' should be non-static and public"), findings.map { it.message })
    }

    @Test
    fun `a junit 4 rule method of a wrong type is reported`() {
        val code = """
            class FooTest {
                @org.junit.Rule
                fun rule() = 0
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Method 'rule' annotated with '@Rule' should be of type 'org.junit.rules.TestRule'"), findings.map { it.message })
    }

    @Test
    fun `a private junit 4 rule getter is reported`() {
        val code = """
            class SomeTestRule : org.junit.rules.TestRule {
                override fun apply(base: org.junit.runners.model.Statement, description: org.junit.runner.Description) = base
            }

            class FooTest {
                @get:org.junit.Rule
                private val rule = SomeTestRule()
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Method 'rule' annotated with '@Rule' should be public"), findings.map { it.message })
    }

    @Test
    fun `a private junit 4 class rule of a wrong type is reported`() {
        val code = """
            object FooTest {
                @org.junit.ClassRule
                private var rule = 0
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Field 'rule' annotated with '@ClassRule' should be public and of type 'org.junit.rules.TestRule'"), findings.map { it.message })
    }

    @Test
    fun `a junit 4 class rule in a companion object passes`() {
        val code = """
            class SomeTestRule : org.junit.rules.TestRule {
                override fun apply(base: org.junit.runners.model.Statement, description: org.junit.runner.Description) = base
            }

            class FooTest {
                companion object {
                    @JvmField
                    @org.junit.ClassRule
                    val rule = SomeTestRule()
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(emptyList(), findings.map { it.message })
    }

    @Test
    fun `a registered extension of a wrong type is reported`() {
        val code = """
            class FooTest {
                @org.junit.jupiter.api.extension.RegisterExtension
                val extension = Any()
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Field 'extension' annotated with '@RegisterExtension' should be of type 'org.junit.jupiter.api.extension.Extension'"), findings.map { it.message })
    }

    @Test
    fun `a class level extension in an instance field is reported`() {
        val code = """
            class MyExt : org.junit.jupiter.api.extension.BeforeAllCallback {
                override fun beforeAll(context: org.junit.jupiter.api.extension.ExtensionContext) {}
            }

            class FooTest {
                @org.junit.jupiter.api.extension.RegisterExtension
                val extension = MyExt()
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("MyExt should be registered at the class level"), findings.map { it.message })
    }

    @Test
    fun `a class level extension in a static field passes`() {
        val code = """
            class MyExt : org.junit.jupiter.api.extension.BeforeAllCallback {
                override fun beforeAll(context: org.junit.jupiter.api.extension.ExtensionContext) {}
            }

            class FooTest {
                companion object {
                    @JvmField
                    @org.junit.jupiter.api.extension.RegisterExtension
                    val extension = MyExt()
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(emptyList(), findings.map { it.message })
    }

    @Test
    fun `a class level extension in a per class test passes`() {
        val code = """
            class MyExt : org.junit.jupiter.api.extension.BeforeAllCallback {
                override fun beforeAll(context: org.junit.jupiter.api.extension.ExtensionContext) {}
            }

            @org.junit.jupiter.api.TestInstance(org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS)
            class FooTest {
                @org.junit.jupiter.api.extension.RegisterExtension
                val extension = MyExt()
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(emptyList(), findings.map { it.message })
    }

    @Test
    fun `a non-static data point is reported`() {
        val code = """
            class FooTest {
                @JvmField
                @org.junit.experimental.theories.DataPoint
                val point: Any? = null

                @org.junit.experimental.theories.DataPoints
                private fun points(): Array<Int> = arrayOf(1)
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(
            listOf(
                "Field 'point' annotated with '@DataPoint' should be static",
                "Method 'points' annotated with '@DataPoints' should be static and public",
            ),
            findings.map { it.message },
        )
    }

    @Test
    fun `a static data point passes`() {
        val code = """
            class FooTest {
                companion object {
                    @JvmField
                    @org.junit.experimental.theories.DataPoint
                    val point: Any? = null
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(emptyList(), findings.map { it.message })
    }

    @Test
    fun `a repeated test with zero repetitions is reported`() {
        val code = """
            import org.junit.jupiter.api.RepeatedTest

            class FooTest {
                @RepeatedTest(0)
                fun test() {}

                @RepeatedTest(2)
                fun other() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("The number of repetitions must be greater than zero"), findings.map { it.message })
    }

    @Test
    fun `a test with a repeated test annotation is reported`() {
        val code = """
            import org.junit.jupiter.api.RepeatedTest
            import org.junit.jupiter.api.Test

            class FooTest {
                @Test
                @RepeatedTest(1)
                fun test() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Suspicious combination of '@Test' and '@RepeatedTest'"), findings.map { it.message })
    }

    @Test
    fun `a test with a value source is reported`() {
        val code = """
            import org.junit.jupiter.api.Test
            import org.junit.jupiter.params.provider.ValueSource

            class FooTest {
                @ValueSource(ints = [1])
                @Test
                fun test(i: Int) {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Suspicious combination of '@ValueSource' and '@Test'"), findings.map { it.message })
    }

    @Test
    fun `a parameterized test without sources is reported`() {
        val code = """
            import org.junit.jupiter.params.ParameterizedTest

            class FooTest {
                @ParameterizedTest
                fun test(value: String) {}

                @ParameterizedTest
                @org.junit.jupiter.params.provider.ArgumentsSources()
                fun empty(value: String) {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(
            listOf(
                "No sources are provided, the suite would be empty",
                "No sources are provided, the suite would be empty",
            ),
            findings.map { it.message },
        )
    }

    @Test
    fun `a parameterized test with a custom source passes`() {
        val code = """
            import org.junit.jupiter.params.ParameterizedTest
            import org.junit.jupiter.params.provider.ArgumentsSource

            class Provider : org.junit.jupiter.params.provider.ArgumentsProvider {
                override fun provideArguments(
                    parameters: org.junit.jupiter.params.ParameterDeclarations,
                    context: org.junit.jupiter.api.extension.ExtensionContext,
                ) = java.util.stream.Stream.empty<org.junit.jupiter.params.provider.Arguments>()
            }

            @ArgumentsSource(Provider::class)
            annotation class Custom

            class FooTest {
                @ParameterizedTest
                @ArgumentsSource(Provider::class)
                fun test(value: String) {}

                @ParameterizedTest
                @Custom
                fun custom(value: String) {}

                @ParameterizedTest
                @org.junit.jupiter.params.provider.CsvSource("a, 1")
                fun csv(value: String, number: Int) {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(emptyList(), findings.map { it.message })
    }

    @Test
    fun `a value source for two parameters is reported`() {
        val code = """
            import org.junit.jupiter.params.ParameterizedTest
            import org.junit.jupiter.params.provider.ValueSource

            class FooTest {
                @ParameterizedTest
                @ValueSource(strings = ["a"])
                fun test(value: String, number: Int) {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Only a single parameter can be provided by '@ValueSource'"), findings.map { it.message })
    }

    @Test
    fun `a value source for one parameter passes`() {
        val code = """
            import org.junit.jupiter.params.ParameterizedTest
            import org.junit.jupiter.params.provider.ValueSource

            class FooTest {
                @ParameterizedTest
                @ValueSource(strings = ["a"])
                fun test(value: String, info: org.junit.jupiter.api.TestInfo) {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(emptyList(), findings.map { it.message })
    }

    @Test
    fun `a value source without values is reported`() {
        val code = """
            import org.junit.jupiter.params.ParameterizedTest
            import org.junit.jupiter.params.provider.ValueSource

            class FooTest {
                @ParameterizedTest
                @ValueSource()
                fun test(value: Int) {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("No value source is defined"), findings.map { it.message })
    }

    @Test
    fun `a value source with two kinds of values is reported`() {
        val code = """
            import org.junit.jupiter.params.ParameterizedTest
            import org.junit.jupiter.params.provider.ValueSource

            class FooTest {
                @ParameterizedTest
                @ValueSource(ints = [1], strings = ["a"])
                fun test(value: Int) {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Exactly one type of input must be provided"), findings.map { it.message })
    }

    @Test
    fun `a method source that does not resolve is reported`() {
        val code = """
            import org.junit.jupiter.params.ParameterizedTest
            import org.junit.jupiter.params.provider.MethodSource

            class FooTest {
                @ParameterizedTest
                @MethodSource("values")
                fun test(value: String) {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Cannot resolve target method source: 'values'"), findings.map { it.message })
    }

    @Test
    fun `a method source that does not resolve in an abstract class passes`() {
        val code = """
            import org.junit.jupiter.params.ParameterizedTest
            import org.junit.jupiter.params.provider.MethodSource

            abstract class FooTest {
                @ParameterizedTest
                @MethodSource("values", "other#values")
                fun test(value: String) {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(emptyList(), findings.map { it.message })
    }

    @Test
    fun `a non-static method source is reported`() {
        val code = """
            import org.junit.jupiter.params.ParameterizedTest
            import org.junit.jupiter.params.provider.MethodSource

            class FooTest {
                @ParameterizedTest
                @MethodSource("values")
                fun test(value: String) {}

                fun values() = listOf("a")
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Method source 'values' must be static"), findings.map { it.message })
    }

    @Test
    fun `a non-static method source in a per class test passes`() {
        val code = """
            import org.junit.jupiter.api.TestInstance
            import org.junit.jupiter.params.ParameterizedTest
            import org.junit.jupiter.params.provider.MethodSource

            @TestInstance(TestInstance.Lifecycle.PER_CLASS)
            class FooTest {
                @ParameterizedTest
                @MethodSource("values")
                fun test(value: String) {}

                fun values() = sequenceOf("a")
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(emptyList(), findings.map { it.message })
    }

    @Test
    fun `a method source with parameters is reported`() {
        val code = """
            import org.junit.jupiter.params.ParameterizedTest
            import org.junit.jupiter.params.provider.MethodSource

            class FooTest {
                @ParameterizedTest
                @MethodSource("values")
                fun test(value: String) {}

                companion object {
                    @JvmStatic
                    fun values(i: Int) = arrayOf("a", i.toString())
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Method source 'values' should have no parameters"), findings.map { it.message })
    }

    @Test
    fun `a method source with a wrong return type is reported`() {
        val code = """
            import org.junit.jupiter.params.ParameterizedTest
            import org.junit.jupiter.params.provider.MethodSource

            class FooTest {
                @ParameterizedTest
                @MethodSource("values")
                fun test(value: String) {}

                companion object {
                    @JvmStatic
                    fun values(): Any = arrayOf("a")
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Method source 'values' must have one of the following return types: 'Stream<*>', 'Iterator<*>', 'Iterable<*>', 'Sequence<*>' or 'Array<*>'"), findings.map { it.message })
    }

    @Test
    fun `a method source of plain values for two parameters is reported`() {
        val code = """
            import org.junit.jupiter.params.ParameterizedTest
            import org.junit.jupiter.params.provider.MethodSource

            class FooTest {
                @ParameterizedTest
                @MethodSource
                fun test(value: String, number: Int) {}

                companion object {
                    @JvmStatic
                    fun test() = listOf("a")
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Multiple parameters have to be wrapped in 'Arguments'"), findings.map { it.message })
    }

    @Test
    fun `a static method source of arguments passes`() {
        val code = """
            import org.junit.jupiter.params.ParameterizedTest
            import org.junit.jupiter.params.provider.Arguments
            import org.junit.jupiter.params.provider.MethodSource

            class Values {
                operator fun iterator(): Iterator<String> = listOf("a").iterator()
            }

            class FooTest {
                @ParameterizedTest
                @MethodSource("stream", "iterator", "array", "custom")
                fun test(value: String, number: Int) {}

                companion object {
                    @JvmStatic
                    fun stream(): java.util.stream.Stream<Arguments> = java.util.stream.Stream.of(Arguments.of("a", 1))

                    @JvmStatic
                    fun iterator(): Iterator<Any> = listOf<Any>().iterator()

                    @JvmStatic
                    fun array(): Array<Array<Any>> = arrayOf(arrayOf("a", 1))

                    @JvmStatic
                    fun custom() = Values()
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(emptyList(), findings.map { it.message })
    }

    @Test
    fun `a null source on a method without parameters is reported`() {
        val code = """
            import org.junit.jupiter.params.ParameterizedTest
            import org.junit.jupiter.params.provider.NullSource

            class FooTest {
                @ParameterizedTest
                @NullSource
                fun test() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("'@NullSource' cannot provide an argument to method because method doesn't have parameters"), findings.map { it.message })
    }

    @Test
    fun `a null source on a method with two parameters is reported`() {
        val code = """
            import org.junit.jupiter.params.ParameterizedTest
            import org.junit.jupiter.params.provider.MethodSource
            import org.junit.jupiter.params.provider.NullSource

            class FooTest {
                @ParameterizedTest
                @NullSource
                @MethodSource("values")
                fun test(value: String?, other: String?) {}

                companion object {
                    @JvmStatic
                    fun values() = listOf(org.junit.jupiter.params.provider.Arguments.of("a", "b"))
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Only a single parameter can be provided by '@NullSource'"), findings.map { it.message })
    }

    @Test
    fun `a null or empty source on a supported parameter passes`() {
        val code = """
            import org.junit.jupiter.params.ParameterizedTest
            import org.junit.jupiter.params.provider.EmptySource
            import org.junit.jupiter.params.provider.NullAndEmptySource
            import org.junit.jupiter.params.provider.NullSource

            class FooTest {
                @ParameterizedTest
                @NullSource
                fun nullable(value: String?) {}

                @ParameterizedTest
                @EmptySource
                fun list(value: List<String>) {}

                @ParameterizedTest
                @EmptySource
                fun array(value: IntArray) {}

                @ParameterizedTest
                @NullAndEmptySource
                fun arrayList(value: java.util.ArrayList<String>?) {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(emptyList(), findings.map { it.message })
    }

    @Test
    fun `an empty source on an unsupported parameter is reported`() {
        val code = """
            import org.junit.jupiter.params.ParameterizedTest
            import org.junit.jupiter.params.provider.EmptySource

            class FooTest {
                @ParameterizedTest
                @EmptySource
                fun test(value: Int) {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("'@EmptySource' cannot provide an argument to method because method has an unsupported parameter of 'Int' type"), findings.map { it.message })
    }

    @Test
    fun `an empty source on wrong parameters is reported`() {
        val code = """
            import org.junit.jupiter.params.ParameterizedTest
            import org.junit.jupiter.params.provider.NullAndEmptySource

            class FooTest {
                @ParameterizedTest
                @NullAndEmptySource
                fun none() {}

                @ParameterizedTest
                @NullAndEmptySource
                fun two(value: String, other: String) {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(
            listOf(
                "'@NullAndEmptySource' cannot provide an argument to method because method doesn't have parameters",
                "Only a single parameter can be provided by '@NullAndEmptySource'",
                "Only a single parameter can be provided by '@NullAndEmptySource'",
            ),
            findings.map { it.message },
        )
    }

    @Test
    fun `an inner class with junit 5 tests without nested annotation is reported`() {
        val code = """
            import org.junit.jupiter.api.Test

            class FooTest {
                inner class Inner {
                    @Test
                    fun test() {}
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Tests in nested class will not be executed"), findings.map { it.message })
    }

    @Test
    fun `a nested annotation on a non-inner class is reported`() {
        val code = """
            import org.junit.jupiter.api.Nested

            class FooTest {
                @Nested
                class Inner
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Tests in nested class will not be executed"), findings.map { it.message })
    }

    @Test
    fun `well formed nested classes pass`() {
        val code = """
            import org.junit.jupiter.api.Nested
            import org.junit.jupiter.api.Test

            class FooTest {
                @Nested
                inner class Inner {
                    @Test
                    fun test() {}
                }

                class Static {
                    @Test
                    fun test() {}
                }
            }

            abstract class Base {
                inner class Inner {
                    @Test
                    fun test() {}
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(emptyList(), findings.map { it.message })
    }

    @Test
    fun `an inner class with junit 4 tests is reported`() {
        val code = """
            import org.junit.Test

            class FooTest {
                inner class Inner {
                    @Test
                    fun test() {}
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Tests in nested class will not be executed"), findings.map { it.message })
    }

    @Test
    fun `an inner class with junit 4 tests in an enclosed runner passes`() {
        val code = """
            import org.junit.Test
            import org.junit.runner.RunWith

            @RunWith(org.junit.experimental.runners.Enclosed::class)
            class FooTest {
                inner class Inner {
                    @Test
                    fun test() {}
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(emptyList(), findings.map { it.message })
    }

    @Test
    fun `malformed junit 3 test methods are reported`() {
        val code = """
            import junit.framework.TestCase

            class FooTest : TestCase() {
                fun testOne() {}

                fun testTwo(): Int = 2

                fun testThree(i: Int) {}

                private fun testFour(i: Int) {}

                companion object {
                    @JvmStatic
                    fun testFive() {}
                }
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(
            listOf(
                "Method 'testTwo' should be public, non-static, have no parameters and of type 'Unit'",
                "Method 'testThree' should be public, non-static, have no parameters and of type 'Unit'",
                "Method 'testFive' should be public, non-static, have no parameters and of type 'Unit'",
            ),
            findings.map { it.message },
        )
    }

    @Test
    fun `a suspend junit 3 test method is reported`() {
        val code = """
            import junit.framework.TestCase

            class FooTest : TestCase() {
                suspend fun testOne() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Method 'testOne' should not be a suspending function"), findings.map { it.message })
    }

    @Test
    fun `a malformed junit 3 set up method is reported`() {
        val code = """
            import junit.framework.TestCase

            class FooTest : TestCase() {
                private fun setUp(i: Int) {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Method 'setUp' should be non-private, non-static, have no parameters and of type 'Unit'"), findings.map { it.message })
    }

    @Test
    fun `a malformed junit 3 suite method is reported`() {
        val code = """
            import junit.framework.TestCase

            class FooTest : TestCase() {
                fun suite() = junit.framework.TestSuite()
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Method 'suite' should be non-private, static and have no parameters"), findings.map { it.message })
    }

    @Test
    fun `well formed junit 3 methods pass`() {
        val code = """
            import junit.framework.TestCase

            class FooTest : TestCase() {
                override fun setUp() {}

                fun helper(i: Int): Int = i

                companion object {
                    @JvmStatic
                    fun suite() = junit.framework.TestSuite()
                }
            }

            class Plain {
                fun testOne(): Int = 1
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(emptyList(), findings.map { it.message })
    }

    @Test
    fun `a junit 3 test class without a usable constructor is reported`() {
        val code = """
            import junit.framework.TestCase

            class FooTest private constructor() : TestCase()

            class BarTest(val foo: Any) : TestCase()
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(
            listOf(
                "Test class 'FooTest' is not constructable because it does not have a 'public' no-arg or single 'String' parameter constructor",
                "Test class 'BarTest' is not constructable because it does not have a 'public' no-arg or single 'String' parameter constructor",
            ),
            findings.map { it.message },
        )
    }

    @Test
    fun `a junit 3 test class with a usable constructor passes`() {
        val code = """
            import junit.framework.TestCase

            class FooTest(name: String) : TestCase(name)

            class BarTest(val foo: Any = 1) : TestCase()

            fun main() {
                class Local(val foo: Any) : TestCase()
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(emptyList(), findings.map { it.message })
    }

    @Test
    fun `a junit 4 test class with two public constructors is reported`() {
        val code = """
            import org.junit.Test

            class FooTest() {
                constructor(value: String) : this()

                @Test
                fun test() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Test class 'FooTest' is not constructable because it should have exactly one 'public' no-arg constructor"), findings.map { it.message })
    }

    @Test
    fun `a junit 4 test class with one public constructor passes`() {
        val code = """
            import org.junit.Test

            class FooTest() {
                private constructor(value: String) : this()

                @Test
                fun test() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(emptyList(), findings.map { it.message })
    }

    @Test
    fun `a private junit 4 test class is reported`() {
        val code = """
            import org.junit.Test

            private class FooTest {
                @Test
                fun test() {}
            }
        """.trimIndent()

        val findings = sut.lintWithContext(environment, code)

        assertEquals(listOf("Test class 'FooTest' is not constructable because it is not 'public'"), findings.map { it.message })
    }
}

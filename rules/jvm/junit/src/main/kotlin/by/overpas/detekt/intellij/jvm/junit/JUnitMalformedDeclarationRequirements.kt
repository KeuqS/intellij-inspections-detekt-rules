package by.overpas.detekt.intellij.jvm.junit

import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.DATA_POINT
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.DATA_POINTS
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.EXTENSION
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.METHOD_RULE
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.REGISTER_EXTENSION
import by.overpas.detekt.intellij.jvm.junit.MalformedClassIds.TEST_RULE
import org.jetbrains.kotlin.name.ClassId

internal enum class StaticRequirement { STATIC, NON_STATIC, STATIC_UNLESS_PER_CLASS, ANY }

internal enum class VisibilityRequirement { PUBLIC, NOT_PRIVATE, ANY }

internal enum class ParameterRequirement { NONE, EACH_CALLBACK, ALL_CALLBACK, JUPITER_TEST }

internal data class SignatureRequirement(
    val annotations: List<ClassId>,
    val static: StaticRequirement,
    val visibility: VisibilityRequirement,
    val isUnitRequired: Boolean = false,
    val superTypes: List<ClassId> = emptyList(),
    val parameters: ParameterRequirement? = null,
)

private val DATA_POINT_REQUIREMENT = SignatureRequirement(
    annotations = listOf(DATA_POINT, DATA_POINTS),
    static = StaticRequirement.STATIC,
    visibility = VisibilityRequirement.PUBLIC,
)

private val RULE_REQUIREMENT = SignatureRequirement(
    annotations = listOf(ORG_JUNIT_RULE),
    static = StaticRequirement.NON_STATIC,
    visibility = VisibilityRequirement.PUBLIC,
    superTypes = listOf(TEST_RULE, METHOD_RULE),
)

private val CLASS_RULE_REQUIREMENT = SignatureRequirement(
    annotations = listOf(ORG_JUNIT_CLASS_RULE),
    static = StaticRequirement.STATIC,
    visibility = VisibilityRequirement.PUBLIC,
    superTypes = listOf(TEST_RULE),
)

private val REGISTER_EXTENSION_REQUIREMENT = SignatureRequirement(
    annotations = listOf(REGISTER_EXTENSION),
    static = StaticRequirement.ANY,
    visibility = VisibilityRequirement.ANY,
    superTypes = listOf(EXTENSION),
)

internal val FIELD_REQUIREMENTS = listOf(
    DATA_POINT_REQUIREMENT,
    RULE_REQUIREMENT,
    CLASS_RULE_REQUIREMENT,
    REGISTER_EXTENSION_REQUIREMENT,
)

internal val METHOD_REQUIREMENTS = listOf(
    DATA_POINT_REQUIREMENT,
    RULE_REQUIREMENT,
    CLASS_RULE_REQUIREMENT,
    SignatureRequirement(
        annotations = listOf(ORG_JUNIT_BEFORE, ORG_JUNIT_AFTER),
        static = StaticRequirement.NON_STATIC,
        visibility = VisibilityRequirement.PUBLIC,
        isUnitRequired = true,
        parameters = ParameterRequirement.NONE,
    ),
    SignatureRequirement(
        annotations = listOf(JUPITER_BEFORE_EACH, JUPITER_AFTER_EACH),
        static = StaticRequirement.NON_STATIC,
        visibility = VisibilityRequirement.NOT_PRIVATE,
        isUnitRequired = true,
        parameters = ParameterRequirement.EACH_CALLBACK,
    ),
    SignatureRequirement(
        annotations = listOf(ORG_JUNIT_BEFORE_CLASS, ORG_JUNIT_AFTER_CLASS),
        static = StaticRequirement.STATIC,
        visibility = VisibilityRequirement.PUBLIC,
        isUnitRequired = true,
        parameters = ParameterRequirement.NONE,
    ),
    SignatureRequirement(
        annotations = listOf(JUPITER_BEFORE_ALL, JUPITER_AFTER_ALL),
        static = StaticRequirement.STATIC_UNLESS_PER_CLASS,
        visibility = VisibilityRequirement.NOT_PRIVATE,
        isUnitRequired = true,
        parameters = ParameterRequirement.ALL_CALLBACK,
    ),
    SignatureRequirement(
        annotations = listOf(ORG_JUNIT_TEST),
        static = StaticRequirement.NON_STATIC,
        visibility = VisibilityRequirement.PUBLIC,
        isUnitRequired = true,
        parameters = ParameterRequirement.NONE,
    ),
    SignatureRequirement(
        annotations = listOf(JUPITER_TEST),
        static = StaticRequirement.NON_STATIC,
        visibility = VisibilityRequirement.NOT_PRIVATE,
        isUnitRequired = true,
        parameters = ParameterRequirement.JUPITER_TEST,
    ),
)

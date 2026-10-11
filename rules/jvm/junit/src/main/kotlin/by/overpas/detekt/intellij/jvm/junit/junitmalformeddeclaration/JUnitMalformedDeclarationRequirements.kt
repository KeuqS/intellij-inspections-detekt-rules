package by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration

import by.overpas.detekt.intellij.jvm.junit.JUPITER_AFTER_ALL
import by.overpas.detekt.intellij.jvm.junit.JUPITER_AFTER_EACH
import by.overpas.detekt.intellij.jvm.junit.JUPITER_BEFORE_ALL
import by.overpas.detekt.intellij.jvm.junit.JUPITER_BEFORE_EACH
import by.overpas.detekt.intellij.jvm.junit.JUPITER_TEST
import by.overpas.detekt.intellij.jvm.junit.ORG_JUNIT_AFTER
import by.overpas.detekt.intellij.jvm.junit.ORG_JUNIT_AFTER_CLASS
import by.overpas.detekt.intellij.jvm.junit.ORG_JUNIT_BEFORE
import by.overpas.detekt.intellij.jvm.junit.ORG_JUNIT_BEFORE_CLASS
import by.overpas.detekt.intellij.jvm.junit.ORG_JUNIT_CLASS_RULE
import by.overpas.detekt.intellij.jvm.junit.ORG_JUNIT_RULE
import by.overpas.detekt.intellij.jvm.junit.ORG_JUNIT_TEST
import by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration.MalformedClassIds.DATA_POINT
import by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration.MalformedClassIds.DATA_POINTS
import by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration.MalformedClassIds.EXTENSION
import by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration.MalformedClassIds.METHOD_RULE
import by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration.MalformedClassIds.REGISTER_EXTENSION
import by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration.MalformedClassIds.TEST_RULE

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

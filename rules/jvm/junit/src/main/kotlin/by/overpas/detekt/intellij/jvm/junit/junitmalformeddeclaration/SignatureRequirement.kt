package by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration

import org.jetbrains.kotlin.name.ClassId

internal data class SignatureRequirement(
    val annotations: List<ClassId>,
    val static: StaticRequirement,
    val visibility: VisibilityRequirement,
    val isUnitRequired: Boolean = false,
    val superTypes: List<ClassId> = emptyList(),
    val parameters: ParameterRequirement? = null,
)

package by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.types.KaErrorType
import org.jetbrains.kotlin.analysis.api.types.KaType

internal fun SignatureRequirement.staticProblem(
    isStatic: Boolean,
    isPerClass: Boolean,
): String? =
    when (static) {
        StaticRequirement.STATIC -> "static".takeUnless { isStatic }
        StaticRequirement.STATIC_UNLESS_PER_CLASS -> "static".takeUnless { isStatic || isPerClass }
        StaticRequirement.NON_STATIC -> "non-static".takeIf { isStatic }
        StaticRequirement.ANY -> null
    }

internal fun SignatureRequirement.visibilityProblem(
    isPublic: Boolean,
    isPrivate: Boolean,
): String? =
    when (visibility) {
        VisibilityRequirement.PUBLIC -> "public".takeUnless { isPublic }
        VisibilityRequirement.NOT_PRIVATE -> "public".takeIf { isPrivate }
        VisibilityRequirement.ANY -> null
    }

context(session: KaSession)
internal fun SignatureRequirement.typeProblem(type: KaType): String? =
    with(session) {
        when {
            type is KaErrorType -> null
            isUnitRequired && !type.isUnitType -> "Unit"
            superTypes.isNotEmpty() && superTypes.none { type.isSubtypeOf(it) } -> superTypes.first().asFqNameString()
            else -> null
        }
    }

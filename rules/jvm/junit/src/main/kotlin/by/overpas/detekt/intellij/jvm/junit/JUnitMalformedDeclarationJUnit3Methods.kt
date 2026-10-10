package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtNamedFunction

context(session: KaSession, settings: MalformedSettings)
internal fun KtNamedFunction.junit3TestMessage(): String? =
    with(session) {
        val isTestMethod = isPublicMember() || valueParameters.isEmpty()
        val hasParameters = hasRequiredParameters() && !symbol.hasMetaAnnotation(JUPITER_PARAMETERIZED_TEST)
        val isMalformed = !symbol.returnType.isUnitType || !isPublicMember() || isJvmStatic() || hasParameters
        "Method '${name.orEmpty()}' should be public, non-static, have no parameters and of type 'Unit'"
            .takeIf { isTestMethod && isMalformed }
    }

context(session: KaSession, settings: MalformedSettings)
internal fun KtNamedFunction.setUpMessage(): String? =
    with(session) {
        val isMalformed = !symbol.returnType.isUnitType || isPrivateMember() || isJvmStatic() || hasRequiredParameters()
        "Method '${name.orEmpty()}' should be non-private, non-static, have no parameters and of type 'Unit'".takeIf {
            isMalformed
        }
    }

context(session: KaSession, settings: MalformedSettings)
internal fun KtNamedFunction.suiteMessage(): String? {
    val isMalformed = isPrivateMember() || !isJvmStatic() || hasRequiredParameters()
    return "Method '${name.orEmpty()}' should be non-private, static and have no parameters".takeIf { isMalformed }
}

context(session: KaSession, settings: MalformedSettings)
private fun KtNamedFunction.hasRequiredParameters(): Boolean =
    valueParameters.any { !it.isIgnorable() }

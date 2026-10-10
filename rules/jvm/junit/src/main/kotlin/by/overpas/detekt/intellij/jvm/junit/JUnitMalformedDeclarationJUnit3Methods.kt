package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtNamedFunction

context(session: KaSession)
internal fun KtNamedFunction.junit3TestMessage(settings: MalformedSettings): String? =
    with(session) {
        val isTestMethod = isPublicMember() || valueParameters.isEmpty()
        val hasParameters = hasRequiredParameters(settings) && !symbol.hasMetaAnnotation(JUPITER_PARAMETERIZED_TEST)
        val isMalformed = !symbol.returnType.isUnitType || !isPublicMember() || isJvmStatic() || hasParameters
        "Method '${name.orEmpty()}' should be public, non-static, have no parameters and of type 'Unit'"
            .takeIf { isTestMethod && isMalformed }
    }

context(session: KaSession)
internal fun KtNamedFunction.setUpMessage(settings: MalformedSettings): String? =
    with(session) {
        val isMalformed = !symbol.returnType.isUnitType || isPrivateMember() || isJvmStatic() || hasRequiredParameters(
            settings,
        )
        "Method '${name.orEmpty()}' should be non-private, non-static, have no parameters and of type 'Unit'".takeIf {
            isMalformed
        }
    }

context(session: KaSession)
internal fun KtNamedFunction.suiteMessage(settings: MalformedSettings): String? {
    val isMalformed = isPrivateMember() || !isJvmStatic() || hasRequiredParameters(settings)
    return "Method '${name.orEmpty()}' should be non-private, static and have no parameters".takeIf { isMalformed }
}

context(session: KaSession)
private fun KtNamedFunction.hasRequiredParameters(settings: MalformedSettings): Boolean =
    valueParameters.any { !it.isIgnorable(settings) }

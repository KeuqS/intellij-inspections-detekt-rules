package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject

context(session: KaSession)
internal fun KtNamedFunction.signatureMalformations(settings: MalformedSettings): List<Malformation> =
    with(session) {
        val ids = symbol.annotations.mapNotNull { it.classId }
        METHOD_REQUIREMENTS.mapNotNull { requirement ->
            requirement.annotations.firstOrNull { it in ids }?.let { methodMessage(requirement, it, settings) }
        }.map { malformation(it) }
    }

context(session: KaSession)
private fun KtNamedFunction.methodMessage(
    requirement: SignatureRequirement,
    annotation: ClassId,
    settings: MalformedSettings,
): String? =
    with(session) {
        val subject = "Method '${name.orEmpty()}'"
        if (isSuspend() && !settings.areSuspendFunctionsAllowed) {
            suspendMessage(subject, annotation)
        } else {
            val isPerClass = containingClassOrObject?.isPerClass() == true
            val problems = SignatureProblems(
                modifiers = listOfNotNull(
                    requirement.staticProblem(isJvmStatic(), isPerClass),
                    requirement.visibilityProblem(isPublicMember(), isPrivateMember()),
                ),
                type = requirement.typeProblem(symbol.returnType),
                parameters = invalidParameters(requirement.parameters, settings),
            )
            problems.takeUnless { it.isEmpty() }?.message(subject, annotation)
        }
    }

package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject

private val SET_UP_NAMES = setOf("setUp", "tearDown")

private const val SUITE_NAME = "suite"

private const val TEST_PREFIX = "test"

internal fun KtNamedFunction.isJUnit3Candidate(): Boolean {
    val name = name.orEmpty()
    return name.startsWith(TEST_PREFIX) || name in SET_UP_NAMES || name == SUITE_NAME
}

context(session: KaSession)
internal fun KtNamedFunction.junit3Message(settings: MalformedSettings): String? {
    val name = name.orEmpty()
    return when {
        containingClassOrObject?.isRunnableJUnit3Case() != true -> null

        isSuspend() && !settings.areSuspendFunctionsAllowed && isJUnit3Candidate() -> suspendMessage(
            "Method '$name'",
            null,
        )

        name.startsWith(TEST_PREFIX) -> junit3TestMessage(settings)

        name in SET_UP_NAMES -> setUpMessage(settings)

        name == SUITE_NAME -> suiteMessage(settings)

        else -> null
    }
}

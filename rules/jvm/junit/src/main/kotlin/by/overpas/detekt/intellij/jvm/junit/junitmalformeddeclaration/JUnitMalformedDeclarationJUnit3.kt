package by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject

private const val SUITE_NAME = "suite"

private const val TEST_PREFIX = "test"

private val SET_UP_NAMES = setOf("setUp", "tearDown")

internal fun KtNamedFunction.isJUnit3Candidate(): Boolean {
    val name = name.orEmpty()
    return name.startsWith(TEST_PREFIX) || name in SET_UP_NAMES || name == SUITE_NAME
}

context(session: KaSession, settings: MalformedSettings)
internal fun KtNamedFunction.junit3Message(): String? {
    val name = name.orEmpty()
    val isSuspendRejected = isSuspend() && !settings.areSuspendFunctionsAllowed
    return when {
        containingClassOrObject?.isRunnableJUnit3Case() != true -> null
        isSuspendRejected && isJUnit3Candidate() -> null.suspendMessage("Method '$name'")
        name.startsWith(TEST_PREFIX) -> junit3TestMessage()
        name in SET_UP_NAMES -> setUpMessage()
        name == SUITE_NAME -> suiteMessage()
        else -> null
    }
}

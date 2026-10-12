package by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration

import by.overpas.detekt.intellij.jvm.junit.JUNIT_FRAMEWORK_TEST_CASE
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedClassSymbol
import org.jetbrains.kotlin.psi.KtClass

private const val JUNIT3_CONSTRUCTOR_REASON =
    "it does not have a 'public' no-arg or single 'String' parameter constructor"

context(session: KaSession)
internal fun KtClass.constructionMessages(): List<String> =
    with(session) {
        val symbol = classSymbol as? KaNamedClassSymbol
        val isJUnit3 = symbol != null && symbol.defaultType.isSubtypeOf(JUNIT_FRAMEWORK_TEST_CASE)
        val isJUnit4 = symbol != null && !isJUnit3 && symbol.hasJUnit4Tests()
        val shapes = symbol?.publicConstructorShapes().orEmpty()
        val reason = when {
            isJUnit3 -> JUNIT3_CONSTRUCTOR_REASON.takeUnless {
                shapes.any { shape -> shape.isEmpty() || shape.singleOrNull()?.isStringType == true }
            }

            isJUnit4 -> JUNIT4_CONSTRUCTOR_REASON.takeUnless { shapes.singleOrNull()?.isEmpty() == true }

            else -> null
        }
        val className = name.orEmpty()
        val isNotPublic = (isJUnit3 || isJUnit4) && !isPublicMember()
        listOfNotNull(
            unconstructableMessage(className, "it is not 'public'").takeIf { isNotPublic },
            reason?.let { unconstructableMessage(className, it) },
        )
    }

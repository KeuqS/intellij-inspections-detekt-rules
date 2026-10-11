package by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject

context(session: KaSession, settings: MalformedSettings)
internal fun KtNamedFunction.functionMalformations(): List<Malformation> =
    if (containingClassOrObject?.isRunnable() == true) {
        buildList {
            addAll(signatureMalformations())
            listOfNotNull(junit3Message(), combinationMessage()).mapTo(this) { malformation(it) }
            addAll(listOfNotNull(repetitionMalformation()))
            addAll(parameterizedMalformations())
        }
    } else {
        emptyList()
    }

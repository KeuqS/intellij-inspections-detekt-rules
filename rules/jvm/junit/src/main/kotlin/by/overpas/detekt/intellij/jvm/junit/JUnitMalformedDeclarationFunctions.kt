package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject

context(session: KaSession)
internal fun KtNamedFunction.functionMalformations(settings: MalformedSettings): List<Malformation> =
    if (containingClassOrObject?.isRunnable() == true) {
        buildList {
            addAll(signatureMalformations(settings))
            listOfNotNull(junit3Message(settings), combinationMessage()).mapTo(this) { malformation(it) }
            addAll(listOfNotNull(repetitionMalformation()))
            addAll(parameterizedMalformations(settings))
        }
    } else {
        emptyList()
    }

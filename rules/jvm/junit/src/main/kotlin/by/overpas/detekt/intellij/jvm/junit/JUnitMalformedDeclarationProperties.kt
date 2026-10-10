package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject

context(session: KaSession)
internal fun KtProperty.propertyMalformations(): List<Malformation> =
    if (containingClassOrObject?.isRunnable() == true) {
        val fieldIds = fieldAnnotationIds().toSet()
        buildList {
            addAll(fieldMessages(fieldIds))
            addAll(getterMessages(getterAnnotationIds().toSet()))
            addAll(listOfNotNull(extensionLevelMessage(fieldIds)))
        }.map { malformation(it) }
    } else {
        emptyList()
    }

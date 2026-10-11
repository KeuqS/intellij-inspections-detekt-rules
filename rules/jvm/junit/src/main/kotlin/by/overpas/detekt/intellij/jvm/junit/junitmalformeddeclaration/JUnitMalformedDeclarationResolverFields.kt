package by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration

import by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration.MalformedClassIds.PARAMETER_RESOLVER
import by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration.MalformedClassIds.REGISTER_EXTENSION
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtProperty

context(session: KaSession)
internal fun KtClassOrObject.hasResolverExtensionField(): Boolean =
    with(session) {
        val companions = companionObjects.flatMap { it.declarations }
        (declarations + companions).filterIsInstance<KtProperty>().any { property ->
            REGISTER_EXTENSION in property.fieldAnnotationIds() &&
                property.symbol.returnType.isSubtypeOf(PARAMETER_RESOLVER)
        }
    }

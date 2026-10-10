package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.psi.KtCallableDeclaration
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtObjectDeclaration

context(session: KaSession)
internal fun KtClass.canBeConvertedToJUnit5(): Boolean =
    JUPITER_TEST.isOnClasspath() &&
        isJUnit4TestClass() &&
        !hasRunWithInHierarchy() &&
        ownAndCompanionMembers().none { it.hasAnnotationEntry(ORG_JUNIT_RULE, ORG_JUNIT_CLASS_RULE) } &&
        functions().flatMap { it.annotationEntriesOf(ORG_JUNIT_TEST) }.all { it.valueArguments.isEmpty() }

private fun KtClass.ownAndCompanionMembers(): List<KtCallableDeclaration> =
    (declarations + companionObjects.flatMap(KtObjectDeclaration::getDeclarations))
        .filterIsInstance<KtCallableDeclaration>()

package by.overpas.detekt.intellij.jvm.junit

import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.annotations.KaAnnotation
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtDeclaration

context(session: KaSession)
internal fun KtDeclaration.findAnnotations(vararg classIds: ClassId): List<KaAnnotation> =
    with(session) { symbol.annotations.filter { it.classId in classIds } }

context(session: KaSession)
internal fun KtDeclaration.hasAnnotation(vararg classIds: ClassId): Boolean =
    with(session) { symbol.annotations.any { it.classId in classIds } }

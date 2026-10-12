package by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration

import by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration.MalformedClassIds.EXTEND_WITH
import by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration.MalformedClassIds.EXTENSIONS
import by.overpas.detekt.intellij.jvm.junit.junitmalformeddeclaration.MalformedClassIds.PARAMETER_RESOLVER
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.annotations.KaAnnotated
import org.jetbrains.kotlin.analysis.api.annotations.KaAnnotation
import org.jetbrains.kotlin.analysis.api.annotations.KaAnnotationValue
import org.jetbrains.kotlin.descriptors.annotations.AnnotationUseSiteTarget
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtAnnotated

context(session: KaSession)
internal fun KaAnnotated.hasMetaAnnotation(classId: ClassId): Boolean =
    hasMetaAnnotation { it.classId == classId }

context(session: KaSession)
internal fun KaAnnotated.hasMetaAnnotation(predicate: (KaAnnotation) -> Boolean): Boolean {
    val visited = mutableSetOf<ClassId>()
    return generateSequence(listOf(this)) { level ->
        with(session) {
            level.asSequence()
                .flatMap { annotated -> annotated.annotations.mapNotNull { it.classId } }
                .filter(visited::add)
                .mapNotNull { findClass(it) }
                .toList()
        }.takeIf { it.isNotEmpty() }
    }.any { level -> level.any { annotated -> annotated.annotations.any { predicate(it) } } }
}

context(session: KaSession)
internal fun KaAnnotated.hasParameterResolver(): Boolean =
    hasMetaAnnotation { it.extendsWithParameterResolver() }

context(session: KaSession)
private fun KaAnnotation.extendsWithParameterResolver(): Boolean =
    with(session) {
        val values = arguments.flatMap { it.expression.flatten() }
        when (classId) {
            EXTEND_WITH -> values.filterIsInstance<KaAnnotationValue.ClassLiteralValue>()
                .any { it.type.isSubtypeOf(PARAMETER_RESOLVER) }

            EXTENSIONS -> values.filterIsInstance<KaAnnotationValue.NestedAnnotationValue>()
                .any { it.annotation.extendsWithParameterResolver() }

            else -> false
        }
    }

internal fun KaAnnotationValue.flatten(): List<KaAnnotationValue> =
    if (this is KaAnnotationValue.ArrayValue) values.flatMap { it.flatten() } else listOf(this)

internal fun KaAnnotation.stringValues(): List<String> =
    arguments.asSequence()
        .filter { it.name.asString() == "value" }
        .flatMap { it.expression.flatten() }
        .filterIsInstance<KaAnnotationValue.ConstantValue>()
        .mapNotNull { it.value.value as? String }
        .toList()

context(session: KaSession)
internal fun KtAnnotated.annotationIds(): List<ClassId> =
    annotationIds { true }

context(session: KaSession)
internal fun KtAnnotated.fieldAnnotationIds(): List<ClassId> =
    annotationIds { it == null || it == AnnotationUseSiteTarget.FIELD }

context(session: KaSession)
internal fun KtAnnotated.getterAnnotationIds(): List<ClassId> =
    annotationIds { it == AnnotationUseSiteTarget.PROPERTY_GETTER }

context(session: KaSession)
private fun KtAnnotated.annotationIds(accepts: (AnnotationUseSiteTarget?) -> Boolean): List<ClassId> =
    with(session) {
        annotationEntries
            .filter { accepts(it.useSiteTarget?.getAnnotationUseSiteTarget()) }
            .mapNotNull { it.typeReference?.type?.expandedSymbol?.classId }
    }

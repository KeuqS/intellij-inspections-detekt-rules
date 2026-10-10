package by.overpas.detekt.intellij.jvm.testframeworks

import org.jetbrains.kotlin.analysis.api.annotations.KaAnnotation
import org.jetbrains.kotlin.analysis.api.symbols.markers.KaAnnotatedSymbol
import org.jetbrains.kotlin.name.ClassId

internal fun KaAnnotatedSymbol.hasAnnotation(classId: ClassId): Boolean =
    annotations.any { it.classId == classId }

internal fun KaAnnotatedSymbol.findAnnotation(classId: ClassId): KaAnnotation? =
    annotations.firstOrNull { it.classId == classId }

internal fun KaAnnotation.hasArgument(name: String): Boolean =
    arguments.any { it.name.asString() == name }

package by.overpas.detekt.intellij.jvm.general

import org.jetbrains.kotlin.name.ClassId

internal val API_STATUS_SCHEDULED_FOR_REMOVAL = ClassId.fromString(
    "org/jetbrains/annotations/ApiStatus.ScheduledForRemoval",
)
internal val API_STATUS_NON_EXTENDABLE = ClassId.fromString("org/jetbrains/annotations/ApiStatus.NonExtendable")
internal val API_STATUS_OVERRIDE_ONLY = ClassId.fromString("org/jetbrains/annotations/ApiStatus.OverrideOnly")
internal val API_STATUS_OBSOLETE = ClassId.fromString("org/jetbrains/annotations/ApiStatus.Obsolete")
internal val JAVA_DEPRECATED = ClassId.fromString("java/lang/Deprecated")
internal val KOTLIN_DEPRECATED = ClassId.fromString("kotlin/Deprecated")
internal val SCALA_DEPRECATED = ClassId.fromString("scala/deprecated")

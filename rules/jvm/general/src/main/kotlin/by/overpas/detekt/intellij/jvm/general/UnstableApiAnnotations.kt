package by.overpas.detekt.intellij.jvm.general

internal val UNSTABLE_API_ANNOTATIONS = listOf(
    "org.jetbrains.annotations.ApiStatus.ScheduledForRemoval",
    "org.jetbrains.annotations.ApiStatus.Experimental",
    "org.jetbrains.annotations.ApiStatus.Internal",
    "com.google.common.annotations.Beta",
    "io.reactivex.annotations.Beta",
    "io.reactivex.annotations.Experimental",
    "rx.annotations.Experimental",
    "rx.annotations.Beta",
    "org.apache.http.annotation.Beta",
    "org.gradle.api.Incubating",
)

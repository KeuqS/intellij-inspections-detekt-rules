package by.overpas.detekt.intellij.jvm.general.unstableapiusage

import by.overpas.detekt.intellij.jvm.general.API_STATUS_SCHEDULED_FOR_REMOVAL
import by.overpas.detekt.intellij.jvm.general.ApiStatusAnnotatedDeclaration
import by.overpas.detekt.intellij.jvm.general.inVersion

internal fun ApiStatusAnnotatedDeclaration.unstableApiUsageMessage(isOverride: Boolean): String {
    val subject = if (isOverride) "Overridden method '${target.name}'" else "'${target.name}'"
    val container = "${container.kind} '${container.name}'"
    return when {
        !isScheduledForRemoval() && isOwnAnnotation -> "$subject is marked unstable with @$presentableAnnotationName"

        !isScheduledForRemoval() ->
            "$subject is declared in unstable $container marked with @$presentableAnnotationName"

        isOwnAnnotation -> "$subject is scheduled for removal in ${removalVersion()}"

        else -> "$subject is declared in $container scheduled for removal in ${removalVersion()}"
    }
}

internal fun ApiStatusAnnotatedDeclaration.unstableSignatureMessage(referencedName: String): String =
    if (isScheduledForRemoval()) {
        "'$referencedName' is scheduled for removal because its signature references ${target.kind} " +
            "'${target.name}' scheduled for removal in ${removalVersion()}"
    } else {
        "'$referencedName' is unstable because its signature references unstable ${target.kind} " +
            "'${target.name}' marked with @$presentableAnnotationName"
    }

private fun ApiStatusAnnotatedDeclaration.isScheduledForRemoval(): Boolean =
    annotationName == API_STATUS_SCHEDULED_FOR_REMOVAL.asFqNameString()

private fun ApiStatusAnnotatedDeclaration.removalVersion(): String =
    inVersion?.takeIf { it.isNotEmpty() }?.let { "version $it" } ?: "a future version"

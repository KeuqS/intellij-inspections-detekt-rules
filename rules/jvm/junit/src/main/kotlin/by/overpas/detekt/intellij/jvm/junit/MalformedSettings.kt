package by.overpas.detekt.intellij.jvm.junit

internal data class MalformedSettings(
    val ignorableAnnotations: Set<String>,
    val areSuspendFunctionsAllowed: Boolean,
)
